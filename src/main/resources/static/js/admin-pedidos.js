document.addEventListener('DOMContentLoaded', function() {
    const pedidosTableBody = document.getElementById('pedidosTableBody');
    const paginationElement = document.getElementById('pagination');
    const filterForm = document.getElementById('filterForm');
    const estadoFilter = document.getElementById('estadoFilter');
    const fechaInicioFilter = document.getElementById('fechaInicioFilter');
    const fechaFinFilter = document.getElementById('fechaFinFilter');
    const clearFiltersButton = document.getElementById('clearFilters');

    let currentPage = 0;
    const pageSize = 10; // Definir el tamaño de página

    // Function to fetch and display orders
    async function fetchAndDisplayPedidos() {
        pedidosTableBody.innerHTML = '<tr><td colspan="7">Cargando pedidos...</td></tr>';
        
        const params = new URLSearchParams();
        params.append('page', currentPage);
        params.append('size', pageSize);

        const estadoId = estadoFilter.value;
        if (estadoId) {
            params.append('estadoId', estadoId);
        }

        const fechaInicio = fechaInicioFilter.value;
        if (fechaInicio) {
            params.append('fechaInicio', fechaInicio);
        }

        const fechaFin = fechaFinFilter.value;
        if (fechaFin) {
            params.append('fechaFin', fechaFin);
        }

        try {
            const response = await fetchWithAuth(`/admin/pedidos/api/list?${params.toString()}`, {
                method: 'GET'
            });
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            const data = await response.json();
            console.log(data); // Para depuración

            pedidosTableBody.innerHTML = ''; // Clear previous data
            if (data.content && data.content.length > 0) {
                data.content.forEach(pedido => {
                    const row = pedidosTableBody.insertRow();
                    row.innerHTML = `
                        <td>${pedido.id}</td>
                        <td>${pedido.numeroPedido}</td>
                        <td>${new Date(pedido.fechaPedido).toLocaleString()}</td>
                        <td>S/.${pedido.total.toFixed(2)}</td>
                        <td>${pedido.estado ? pedido.estado.nombre : 'N/A'}</td>
                        <td>${pedido.usuario ? pedido.usuario.nombreCompleto : 'N/A'}</td>
                        <td>
                            <a href="/admin/pedidos/${pedido.id}" class="btn btn-info btn-sm">Ver</a>
                            <!-- Más acciones como editar o eliminar si es necesario -->
                        </td>
                    `;
                });
            } else {
                pedidosTableBody.innerHTML = '<tr><td colspan="7">No se encontraron pedidos.</td></tr>';
            }
            updatePagination(data);
        } catch (error) {
            console.error('Error al obtener pedidos:', error);
            pedidosTableBody.innerHTML = `<tr><td colspan="7" class="text-danger">Error al cargar pedidos: ${error.message}. Por favor, intente de nuevo.</td></tr>`;
        }
    }

    // Function to update pagination controls
    function updatePagination(data) {
        paginationElement.innerHTML = ''; // Clear previous pagination
        const totalPages = data.totalPages;

        if (totalPages > 1) {
            const createPageItem = (pageNumber, text, isDisabled, isActive) => {
                const li = document.createElement('li');
                li.classList.add('page-item');
                if (isDisabled) li.classList.add('disabled');
                if (isActive) li.classList.add('active');

                const a = document.createElement('a');
                a.classList.add('page-link');
                a.href = '#';
                a.innerHTML = text;
                a.addEventListener('click', (e) => {
                    e.preventDefault();
                    if (!isDisabled && !isActive) {
                        currentPage = pageNumber;
                        fetchAndDisplayPedidos();
                    }
                });
                li.appendChild(a);
                return li;
            };

            paginationElement.appendChild(createPageItem(0, '&laquo;', currentPage === 0)); // First page
            paginationElement.appendChild(createPageItem(currentPage - 1, '&lsaquo;', currentPage === 0)); // Previous page

            let startPage = Math.max(0, currentPage - 2);
            let endPage = Math.min(totalPages - 1, currentPage + 2);

            if (totalPages > 5) { // Show a maximum of 5 pages
                if (currentPage < 2) {
                    endPage = 4;
                } else if (currentPage > totalPages - 3) {
                    startPage = totalPages - 5;
                }
            }

            for (let i = startPage; i <= endPage; i++) {
                paginationElement.appendChild(createPageItem(i, i + 1, false, i === currentPage));
            }

            paginationElement.appendChild(createPageItem(currentPage + 1, '&rsaquo;', currentPage === totalPages - 1)); // Next page
            paginationElement.appendChild(createPageItem(totalPages - 1, '&raquo;', currentPage === totalPages - 1)); // Last page
        }
    }

    // Event listener for filter form submission
    filterForm.addEventListener('submit', function(e) {
        e.preventDefault();
        currentPage = 0; // Reset to first page on filter
        fetchAndDisplayPedidos();
    });

    // Event listener for clear filters button
    clearFiltersButton.addEventListener('click', function() {
        estadoFilter.value = '';
        fechaInicioFilter.value = '';
        fechaFinFilter.value = '';
        currentPage = 0;
        fetchAndDisplayPedidos();
    });

    // Initial load of orders
    fetchAndDisplayPedidos();

    // Function to fetch and populate EstadoPedido filter options
    async function fetchAndPopulateEstados() {
        try {
            const response = await fetchWithAuth('/api/admin/pedidos/estados', {
                method: 'GET'
            });
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            const estados = await response.json();

            estadoFilter.innerHTML = '<option value="">Todos los Estados</option>'; // Reset and add default option
            estados.forEach(estado => {
                const option = document.createElement('option');
                option.value = estado.id;
                option.textContent = estado.nombre;
                estadoFilter.appendChild(option);
            });

        } catch (error) {
            console.error('Error al obtener estados de pedido:', error);
        }
    }

    fetchAndPopulateEstados();
});

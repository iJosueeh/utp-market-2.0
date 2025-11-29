document.addEventListener('DOMContentLoaded', function () {
    const categoriaFilter = document.getElementById('categoria-filter');
    const estadoFilter = document.getElementById('estado-filter');
    const productosTableBody = document.getElementById('productos-table-body');
    const paginationControls = document.getElementById('pagination-controls');

    let currentPage = 0;
    const pageSize = 10;

    async function cargarProductos(page = 0, categoria = '', estado = '') {
        try {
            const url = `/admin/productos/api?page=${page}&size=${pageSize}&categoria=${categoria}&estado=${estado}`;
            const response = await fetch(url);

            if (!response.ok) {
                throw new Error('Error al cargar los productos. Estado: ' + response.status);
            }

            const pageData = await response.json();
            renderizarTabla(pageData.content);
            renderizarPaginacion(pageData);
        } catch (error) {
            console.error('Error en la solicitud fetch:', error);
            productosTableBody.innerHTML = `<tr><td colspan="9" class="text-center">Error al cargar los productos.</td></tr>`;
        }
    }

    function renderizarTabla(productos) {
        productosTableBody.innerHTML = '';
        if (productos.length === 0) {
            productosTableBody.innerHTML = `<tr><td colspan="9" class="text-center">No se encontraron productos.</td></tr>`;
            return;
        }

        productos.forEach(producto => {
            const row = `
                <tr>
                    <td>${producto.id}</td>
                    <td><img src="${producto.imagenUrl}" alt="${producto.nombre}" style="width: 50px; height: auto;"></td>
                    <td>${producto.nombre}</td>
                    <td>${producto.categoria}</td>
                    <td>S/ ${producto.precio.toFixed(2)}</td>
                    <td>${producto.stock}</td>
                    <td>
                        <span class="badge ${producto.estado === 'Activo' ? 'bg-success' : 'bg-danger'}">
                            ${producto.estado}
                        </span>
                    </td>
                    <td>${producto.vendedor}</td>
                    <td>
                        <button class="btn btn-sm btn-primary" onclick="editarProducto(${producto.id})">Editar</button>
                        <button class="btn btn-sm btn-danger" onclick="eliminarProducto(${producto.id})">Eliminar</button>
                    </td>
                </tr>
            `;
            productosTableBody.insertAdjacentHTML('beforeend', row);
        });
    }

    function renderizarPaginacion(pageData) {
        paginationControls.innerHTML = '';
        currentPage = pageData.number;
        const totalPages = pageData.totalPages;

        // Botón "Anterior"
        paginationControls.insertAdjacentHTML('beforeend', `
            <li class="page-item ${pageData.first ? 'disabled' : ''}">
                <a class="page-link" href="#" data-page="${currentPage - 1}">Anterior</a>
            </li>
        `);

        // Números de página
        for (let i = 0; i < totalPages; i++) {
            paginationControls.insertAdjacentHTML('beforeend', `
                <li class="page-item ${i === currentPage ? 'active' : ''}">
                    <a class="page-link" href="#" data-page="${i}">${i + 1}</a>
                </li>
            `);
        }

        // Botón "Siguiente"
        paginationControls.insertAdjacentHTML('beforeend', `
            <li class="page-item ${pageData.last ? 'disabled' : ''}">
                <a class="page-link" href="#" data-page="${currentPage + 1}">Siguiente</a>
            </li>
        `);
    }

    async function cargarCategorias() {
        try {
            const response = await fetch('/api/categorias');
            if (!response.ok) {
                throw new Error('Error al cargar las categorías. Estado: ' + response.status);
            }
            const categorias = await response.json();
            categorias.forEach(cat => {
                categoriaFilter.insertAdjacentHTML('beforeend', `<option value="${cat.nombre}">${cat.nombre}</option>`);
            });
        } catch (error) {
            console.error('Error en la solicitud fetch de categorías:', error);
        }
    }

    async function cargarEstados() {
        try {
            const response = await fetch('/api/estados-producto');
            if (!response.ok) {
                throw new Error('Error al cargar los estados. Estado: ' + response.status);
            }
            const estados = await response.json();
            estados.forEach(estado => {
                estadoFilter.insertAdjacentHTML('beforeend', `<option value="${estado.nombre}">${estado.nombre}</option>`);
            });
        } catch (error) {
            console.error('Error en la solicitud fetch de estados:', error);
        }
    }

    // --- Event Listeners ---
    categoriaFilter.addEventListener('change', () => {
        cargarProductos(0, categoriaFilter.value, estadoFilter.value);
    });

    estadoFilter.addEventListener('change', () => {
        cargarProductos(0, categoriaFilter.value, estadoFilter.value);
    });

    paginationControls.addEventListener('click', (e) => {
        e.preventDefault();
        const target = e.target;
        if (target.tagName === 'A' && !target.parentElement.classList.contains('disabled')) {
            const page = parseInt(target.getAttribute('data-page'), 10);
            cargarProductos(page, categoriaFilter.value, estadoFilter.value);
        }
    });
    
    // --- Funciones de acciones (a implementar) ---
    window.editarProducto = function(id) {
        window.location.href = `/admin/productos/edit/${id}`;
    };

    window.eliminarProducto = async function(id) {
        if (confirm(`¿Está seguro de que desea eliminar el producto con ID: ${id}?`)) {
            try {
                const response = await fetch(`/admin/productos/api/${id}`, {
                    method: 'DELETE'
                });

                if (!response.ok) {
                    throw new Error('Error al eliminar el producto. Estado: ' + response.status);
                }

                cargarProductos(currentPage, categoriaFilter.value, estadoFilter.value);
            } catch (error) {
                console.error('Error al eliminar el producto:', error);
                alert('Error al eliminar el producto.');
            }
        }
    };


    // Carga inicial
    cargarProductos(currentPage);
    cargarCategorias();
    cargarEstados();
});

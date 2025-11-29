document.addEventListener('DOMContentLoaded', function () {
    const API_BASE_URL = '/admin/productos/api';
    const EDIT_MODAL_FRAGMENT_URL = '/admin/productos/fragments/edit-modal';
    const DELETE_MODAL_FRAGMENT_URL = '/admin/productos/fragments/delete-modal';
    const CATEGORIAS_API_URL = '/api/admin/categorias';
    const ESTADOS_API_URL = '/api/admin/estados-producto';

    const productosTableBody = document.getElementById('productos-table-body');
    const paginationControls = document.getElementById('pagination-controls');
    const categoriaFilter = document.getElementById('categoria-filter');
    const estadoFilter = document.getElementById('estado-filter');

    const genericModal = new bootstrap.Modal(document.getElementById('genericModal'));
    const modalLabel = document.getElementById('genericModalLabel');
    const modalBody = document.getElementById('genericModalBody');
    const modalConfirmBtn = document.getElementById('genericModalConfirmBtn');

    let currentPage = 0;
    const pageSize = 10;
    let currentDeleteId = null;
    let currentEditId = null;

    async function fetchAndRenderProductos() {
        const categoria = categoriaFilter.value;
        const estado = estadoFilter.value;
        try {
            const response = await fetch(`${API_BASE_URL}?page=${currentPage}&size=${pageSize}&categoria=${categoria}&estado=${estado}`);
            if (!response.ok) throw new Error('Error al cargar los productos');
            const data = await response.json();
            renderTable(data.content);
            renderPagination(data);
        } catch (error) {
            console.error('Error:', error);
            productosTableBody.innerHTML = `<tr><td colspan="9" class="text-center">Error al cargar los productos.</td></tr>`;
        }
    }

    function renderTable(productos) {
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
                    <td><span class="badge bg-primary">${producto.estado}</span></td>
                    <td>${producto.vendedor}</td>
                    <td>
                        <button class="btn btn-sm btn-info btn-edit" data-id="${producto.id}" title="Editar"><i class="bi bi-pencil-fill"></i></button>
                        <button class="btn btn-sm btn-danger btn-delete" data-id="${producto.id}" title="Eliminar"><i class="bi bi-trash-fill"></i></button>
                    </td>
                </tr>
            `;
            productosTableBody.insertAdjacentHTML('beforeend', row);
        });
    }

    function renderPagination(pageData) {
        paginationControls.innerHTML = '';
        const totalPages = pageData.totalPages;

        if (totalPages <= 1) return;

        // Previous button
        const prevLi = document.createElement('li');
        prevLi.className = `page-item ${pageData.first ? 'disabled' : ''}`;
        const prevA = document.createElement('a');
        prevA.className = 'page-link';
        prevA.href = '#';
        prevA.innerText = 'Anterior';
        prevA.addEventListener('click', (e) => {
            e.preventDefault();
            if (!pageData.first) {
                currentPage--;
                fetchAndRenderProductos();
            }
        });
        prevLi.appendChild(prevA);
        paginationControls.appendChild(prevLi);

        // Page numbers
        for (let i = 0; i < totalPages; i++) {
            const pageLi = document.createElement('li');
            pageLi.className = `page-item ${i === currentPage ? 'active' : ''}`;
            const pageA = document.createElement('a');
            pageA.className = 'page-link';
            pageA.href = '#';
            pageA.innerText = i + 1;
            pageA.addEventListener('click', (e) => {
                e.preventDefault();
                currentPage = i;
                fetchAndRenderProductos();
            });
            pageLi.appendChild(pageA);
            paginationControls.appendChild(pageLi);
        }

        // Next button
        const nextLi = document.createElement('li');
        nextLi.className = `page-item ${pageData.last ? 'disabled' : ''}`;
        const nextA = document.createElement('a');
        nextA.className = 'page-link';
        nextA.href = '#';
        nextA.innerText = 'Siguiente';
        nextA.addEventListener('click', (e) => {
            e.preventDefault();
            if (!pageData.last) {
                currentPage++;
                fetchAndRenderProductos();
            }
        });
        nextLi.appendChild(nextA);
        paginationControls.appendChild(nextLi);
    }

    categoriaFilter.addEventListener('change', () => {
        currentPage = 0;
        fetchAndRenderProductos();
    });

    estadoFilter.addEventListener('change', () => {
        currentPage = 0;
        fetchAndRenderProductos();
    });

    productosTableBody.addEventListener('click', function (e) {
        const editBtn = e.target.closest('.btn-edit');
        const deleteBtn = e.target.closest('.btn-delete');
        if (editBtn) {
            const id = editBtn.dataset.id;
            openEditModal(id);
        } else if (deleteBtn) {
            const id = deleteBtn.dataset.id;
            openDeleteModal(id);
        }
    });

    async function openEditModal(id) {
        currentEditId = id;

        try {
            const fragmentResponse = await fetch(EDIT_MODAL_FRAGMENT_URL);
            if (!fragmentResponse.ok) throw new Error('Error al cargar el fragmento del modal de edición.');
            modalBody.innerHTML = await fragmentResponse.text();

            const categoriaSelect = modalBody.querySelector('#edit-product-categoria');
            const estadoSelect = modalBody.querySelector('#edit-product-estado');

            if (!categoriaSelect || !estadoSelect) {
                 console.error('Error: No se pudieron encontrar los SELECTs en el fragmento inyectado. Revise IDs del fragmento.');
                 alert('Error interno al inicializar el formulario.');
                 return;
            }

            // Debugging: Log URL for states
            console.log('Fetching estados from:', ESTADOS_API_URL);

            await Promise.all([
                populateSelect(categoriaSelect, CATEGORIAS_API_URL, 'id', 'nombre'),
                populateSelect(estadoSelect, ESTADOS_API_URL, 'id', 'nombre')
            ]);

            const response = await fetch(`${API_BASE_URL}/${id}`);
            if (!response.ok) throw new Error('Error al cargar datos del producto.');
            const producto = await response.json();
            // Debugging: Log estadoId from product
            console.log('Producto.estadoId received:', producto.estadoId);
            fillEditForm(producto);
            // Debugging: Log selected value after fillEditForm
            console.log('Estado select value after fillEditForm:', estadoSelect.value);
            console.log('Estado select innerHTML after fillEditForm:', estadoSelect.innerHTML);

            modalLabel.textContent = 'Editar Producto';
            modalConfirmBtn.textContent = 'Guardar Cambios';
            modalConfirmBtn.classList.remove('btn-danger');
            modalConfirmBtn.classList.add('btn-primary');
            modalConfirmBtn.onclick = () => handleUpdateProduct(id);
            genericModal.show();
        } catch (error) {
            console.error('Error:', error);
            alert(`Error al cargar la plantilla de edición: ${error.message}`);
        }
    }

    async function populateSelect(selectElement, url, valueField, textField) {
        try {
            const response = await fetch(url);
            if (!response.ok) throw new Error(`Error al cargar datos desde ${url}`);
            const data = await response.json();
            console.log(`Data received for ${selectElement.id} from ${url}:`, data); // Debugging
            selectElement.innerHTML = '';
            data.forEach(item => {
                const option = document.createElement('option');
                option.value = item[valueField];
                option.textContent = item[textField];
                selectElement.appendChild(option);
                console.log(`Added option to ${selectElement.id}: value='${option.value}', text='${option.textContent}'`); // Debugging
            });
        } catch (error) {
            console.error(error);
        }
    }

    function fillEditForm(producto) {
        modalBody.querySelector('#edit-product-id').value = producto.id;
        modalBody.querySelector('#edit-product-nombre').value = producto.nombre;
        modalBody.querySelector('#edit-product-precio').value = producto.precio;
        modalBody.querySelector('#edit-product-stock').value = producto.stock;
        modalBody.querySelector('#edit-product-categoria').value = producto.categoriaId;
        modalBody.querySelector('#edit-product-estado').value = String(producto.estadoId); // Explicitly convert to string for debugging
        modalBody.querySelector('#edit-product-imagen').value = producto.imagenUrl;
    }

    async function handleUpdateProduct(id) {
        const form = document.getElementById('edit-product-form');

        if (!form) {
             console.error('Error: Formulario de edición no encontrado.');
             return;
        }

        if (!form.checkValidity()) {
            form.classList.add('was-validated');
            return;
        }

        const updatedData = {
            nombre: document.getElementById('edit-product-nombre').value,
            precio: parseFloat(document.getElementById('edit-product-precio').value),
            stock: parseInt(document.getElementById('edit-product-stock').value, 10),
            categoriaId: parseInt(document.getElementById('edit-product-categoria').value, 10),
            estadoId: parseInt(document.getElementById('edit-product-estado').value, 10),
            imagenUrl: document.getElementById('edit-product-imagen').value,
        };

        try {
            const response = await fetch(`${API_BASE_URL}/${id}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(updatedData),
            });
            if (!response.ok) throw new Error('Error al actualizar el producto.');

            genericModal.hide();
            fetchAndRenderProductos(); // Recargar la tabla
            showToast('Producto actualizado con éxito.');

        } catch (error) {
            console.error('Error:', error);
            alert(error.message);
        }
    }

    async function openDeleteModal(id) {
        currentDeleteId = id;

        try {
            const fragmentResponse = await fetch(DELETE_MODAL_FRAGMENT_URL);
            if (!fragmentResponse.ok) throw new Error('Error al cargar el fragmento del modal de eliminación.');
            modalBody.innerHTML = await fragmentResponse.text();

            const itemNameElement = modalBody.querySelector('#delete-item-name');
            if (itemNameElement) {
                itemNameElement.textContent = `el producto con ID ${id}`;
            }

            modalLabel.textContent = 'Confirmar Eliminación';
            modalConfirmBtn.textContent = 'Eliminar';
            modalConfirmBtn.classList.remove('btn-primary');
            modalConfirmBtn.classList.add('btn-danger');
            modalConfirmBtn.onclick = () => handleDeleteProduct(id);
            genericModal.show();
        } catch (error) {
            console.error('Error:', error);
            alert(`Error al cargar la plantilla de eliminación: ${error.message}`);
        }
    }

    async function handleDeleteProduct(id) {
        try {
            const response = await fetch(`${API_BASE_URL}/${id}`, { method: 'DELETE' });
            if (!response.ok) {
                 if (response.status === 409) { // Conflict
                    const errorData = await response.json();
                    throw new Error(errorData.message || 'Conflicto al eliminar el producto.');
                }
                throw new Error('Error al eliminar el producto.');
            }
            genericModal.hide();
            fetchAndRenderProductos(); // Recargar la tabla
            showToast('Producto eliminado con éxito.');
        } catch (error) {
            console.error('Error:', error);
            genericModal.hide();
            alert(error.message);
        }
    }

    function showToast(message) {
        const toastContainer = document.getElementById('toast-container');
        if (!toastContainer) {
            console.error('Toast container not found');
            return;
        }
        const toast = new bootstrap.Toast(toastContainer);
        const toastBody = toastContainer.querySelector('.toast-body');
        if (toastBody) {
            toastBody.textContent = message;
        }
        toast.show();
    }

    fetchAndRenderProductos();
});
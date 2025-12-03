document.addEventListener('DOMContentLoaded', function() {
    const pedidoId = window.location.pathname.split('/').pop(); // Get ID from URL
    const selectEstado = document.getElementById('selectEstado');
    const btnActualizarEstado = document.getElementById('btnActualizarEstado');
    const updateStatusMessage = document.getElementById('updateStatusMessage');

    if (!pedidoId) {
        console.error('No se encontró el ID del pedido en la URL.');
        document.querySelector('.container-fluid').innerHTML = '<div class="alert alert-danger" role="alert">Error: ID de pedido no encontrado.</div>';
        return;
    }

    let allEstados = []; // To store all fetched states

    async function fetchAndPopulateEstados() {
        try {
            const response = await fetchWithAuth('/api/admin/pedidos/estados', { method: 'GET' });
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            allEstados = await response.json(); // Store all states
            selectEstado.innerHTML = ''; // Clear previous options

            allEstados.forEach(estado => {
                const option = document.createElement('option');
                option.value = estado.id;
                option.textContent = estado.nombre;
                selectEstado.appendChild(option);
            });
        } catch (error) {
            console.error('Error al obtener estados de pedido:', error);
            selectEstado.innerHTML = '<option value="">Error al cargar estados</option>';
            selectEstado.disabled = true;
            btnActualizarEstado.disabled = true;
        }
    }

    async function fetchAndDisplayPedidoDetalle() {
        // ... (existing code for fetching and displaying pedido details)
        try {
            const response = await fetchWithAuth(`/admin/pedidos/api/${pedidoId}`, { method: 'GET' });
            if (!response.ok) {
                if (response.status === 404) {
                    throw new Error('Pedido no encontrado.');
                }
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            const pedido = await response.json();
            console.log('Detalle del Pedido:', pedido);

            // Populate general info
            document.getElementById('numeroPedido').textContent = pedido.numeroPedido;
            document.getElementById('pedidoIdDisplay').textContent = pedido.id;
            document.getElementById('numeroPedidoDisplay').textContent = pedido.numeroPedido;
            document.getElementById('fechaPedidoDisplay').textContent = new Date(pedido.fechaPedido).toLocaleString();
            document.getElementById('totalDisplay').textContent = pedido.total.toFixed(2);
            document.getElementById('estadoDisplay').textContent = pedido.estado ? pedido.estado.nombre : 'N/A';
            document.getElementById('estadoDisplay').className = `badge ${getEstadoBadgeClass(pedido.estado.nombre)}`; // Apply badge styling
            document.getElementById('transactionIdDisplay').textContent = pedido.transactionId || 'N/A';

            // Set current status in the dropdown
            if (pedido.estado && selectEstado) {
                selectEstado.value = pedido.estado.id;
            }

            // Populate user and shipping info
            document.getElementById('clienteNombreDisplay').textContent = pedido.usuario ? pedido.usuario.nombreCompleto : 'N/A';
            document.getElementById('clienteIdDisplay').textContent = pedido.usuario ? pedido.usuario.id : 'N/A';
            document.getElementById('metodoPagoDisplay').textContent = pedido.metodoPago ? pedido.metodoPago.nombre : 'N/A';
            document.getElementById('direccionCalleDisplay').textContent = pedido.direccion ? pedido.direccion.calle : 'N/A';
            document.getElementById('direccionDistritoDisplay').textContent = pedido.direccion ? pedido.direccion.distrito : 'N/A';

            // Populate items table
            const itemsPedidoBody = document.getElementById('itemsPedidoBody');
            itemsPedidoBody.innerHTML = '';
            if (pedido.items && pedido.items.length > 0) {
                pedido.items.forEach(item => {
                    const row = itemsPedidoBody.insertRow();
                    row.innerHTML = `
                        <td>${item.producto ? item.producto.nombre : 'Producto Desconocido'}</td>
                        <td>${item.cantidad}</td>
                        <td>S/.${item.precioUnitario != null ? item.precioUnitario.toFixed(2) : 'N/A'}</td>
                        <td>S/.${item.subtotal != null ? item.subtotal.toFixed(2) : 'N/A'}</td>
                    `;
                });
            } else {
                itemsPedidoBody.innerHTML = '<tr><td colspan="4">No hay artículos en este pedido.</td></tr>';
            }

            // Populate history table
            const historialEstadoBody = document.getElementById('historialEstadoBody');
            historialEstadoBody.innerHTML = '';
            if (pedido.historialEstadoPedidos && pedido.historialEstadoPedidos.length > 0) {
                // Sort history by date in descending order (most recent first)
                pedido.historialEstadoPedidos.sort((a, b) => new Date(b.fechaCambio) - new Date(a.fechaCambio));
                
                pedido.historialEstadoPedidos.forEach(historial => {
                    const row = historialEstadoBody.insertRow();
                    row.innerHTML = `
                        <td>${new Date(historial.fechaCambio).toLocaleString()}</td>
                        <td>${historial.estadoAnterior ? historial.estadoAnterior.nombre : 'N/A'}</td>
                        <td>${historial.estadoNuevo ? historial.estadoNuevo.nombre : 'N/A'}</td>
                        <td>${historial.usuarioResponsable ? historial.usuarioResponsable.nombreCompleto : 'N/A'}</td>
                    `;
                });
            } else {
                historialEstadoBody.innerHTML = '<tr><td colspan="4">No hay historial de estado para este pedido.</td></tr>';
            }


        } catch (error) {
            console.error('Error al obtener detalle del pedido:', error);
            document.querySelector('.container-fluid').innerHTML = `<div class="alert alert-danger" role="alert">Error al cargar el detalle del pedido: ${error.message}</div>`;
        }
    }

    async function handleUpdateEstado() {
        const newEstadoId = selectEstado.value;
        if (!newEstadoId) {
            displayStatusMessage('Por favor, selecciona un nuevo estado.', 'alert-warning');
            return;
        }

        try {
            updateStatusMessage.style.display = 'block';
            updateStatusMessage.className = 'alert alert-info';
            updateStatusMessage.textContent = 'Actualizando estado...';
            btnActualizarEstado.disabled = true;
            selectEstado.disabled = true;

            const response = await fetchWithAuth(`/admin/pedidos/api/${pedidoId}/estado`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ newEstadoId: parseInt(newEstadoId) })
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || `HTTP error! status: ${response.status}`);
            }

            const updatedPedido = await response.json();
            displayStatusMessage('Estado actualizado exitosamente.', 'alert-success');
            // Re-render the detail page with updated info
            fetchAndDisplayPedidoDetalle();

        } catch (error) {
            console.error('Error al actualizar estado:', error);
            displayStatusMessage(`Error al actualizar estado: ${error.message}`, 'alert-danger');
        } finally {
            btnActualizarEstado.disabled = false;
            selectEstado.disabled = false;
        }
    }

    function displayStatusMessage(message, type) {
        updateStatusMessage.className = `alert ${type}`;
        updateStatusMessage.textContent = message;
        updateStatusMessage.style.display = 'block';
        setTimeout(() => {
            updateStatusMessage.style.display = 'none';
        }, 5000); // Hide after 5 seconds
    }

    function getEstadoBadgeClass(estadoNombre) {
        switch (estadoNombre) {
            case 'Pendiente': return 'bg-warning text-dark';
            case 'En proceso': return 'bg-info text-dark';
            case 'Listo para recoger': return 'bg-primary';
            case 'Entregado': return 'bg-success';
            case 'Cancelado': return 'bg-danger';
            default: return 'bg-secondary';
        }
    }

    // Initial calls
    fetchAndPopulateEstados();
    fetchAndDisplayPedidoDetalle();

    // Event listener for update button
    btnActualizarEstado.addEventListener('click', handleUpdateEstado);
});


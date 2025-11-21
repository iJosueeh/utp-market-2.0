document.addEventListener('DOMContentLoaded', function () {
    // Lógica para el gráfico de ventas
    const ventasChartCanvas = document.getElementById('ventasChart');
    if (ventasChartCanvas) {
        fetch('/admin/reportes/api/ventas-semanales')
            .then(response => response.json())
            .then(data => {
                const ctx = ventasChartCanvas.getContext('2d');
                new Chart(ctx, {
                    type: 'line',
                    data: {
                        labels: data.labels,
                        datasets: [{
                            label: 'Ventas (S/)',
                            data: data.data,
                            backgroundColor: 'rgba(181, 13, 48, 0.2)',
                            borderColor: '#B50D30',
                            borderWidth: 2,
                            tension: 0.3,
                            fill: true
                        }]
                    },
                    options: {
                        responsive: true,
                        scales: {
                            y: {
                                beginAtZero: true
                            }
                        },
                        plugins: {
                            legend: {
                                display: false
                            }
                        }
                    }
                });
            })
            .catch(error => console.error('Error al cargar datos para el gráfico:', error));
    }

    // Lógica para el toggler del sidebar
    const sidebarToggler = document.getElementById('sidebar-toggler');
    const sidebar = document.querySelector('.admin-sidebar');

    if (sidebarToggler && sidebar) {
        sidebarToggler.addEventListener('click', function (event) {
            event.stopPropagation(); // Prevenir que el click en el botón cierre el sidebar inmediatamente
            sidebar.classList.toggle('is-open');
        });
    }

    // Lógica para cerrar el sidebar al hacer click fuera de él
    // Atamos el evento al body para detectar clicks en cualquier parte
    document.body.addEventListener('click', function (event) {
        // Si el sidebar está abierto Y el click no fue dentro del sidebar Y no fue en el toggler
        if (sidebar && sidebar.classList.contains('is-open') &&
            !sidebar.contains(event.target) && !sidebarToggler.contains(event.target)) {
            sidebar.classList.remove('is-open');
        }
    });});


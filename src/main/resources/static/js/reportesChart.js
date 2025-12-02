async function cargarReporteVentas() {
    const loadingMsg = document.getElementById("loadingMsg");
    const canvas = document.getElementById("ventasChart");

    try {
        const resp = await fetch("/admin/reportes/api/ventas-semanales");

        if (!resp.ok) {
            throw new Error("Error al obtener datos del reporte");
        }

        const json = await resp.json();

        // Ocultar "Cargando..."
        loadingMsg.style.display = "none";
        canvas.style.display = "block";

        // Crear gráfico
        new Chart(canvas, {
            type: "bar",
            data: {
                labels: json.labels,
                datasets: [{
                    label: "Ventas (S/.)",
                    data: json.data,
                    borderWidth: 2,
                    backgroundColor: "rgba(75, 192, 192, 0.55)",
                    borderColor: "rgba(75, 192, 192, 1)",
                    hoverBackgroundColor: "rgba(75, 192, 192, 0.75)",
                    borderRadius: 6
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    y: { beginAtZero: true },
                    x: {}
                }
            }
        });

    } catch (err) {
        loadingMsg.style.color = "red";
        loadingMsg.innerText = "No se pudo cargar el reporte";
    }
}

cargarReporteVentas();

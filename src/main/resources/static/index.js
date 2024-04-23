window.onload = () => {
    const loader = document.getElementById("loader");

    const botons = document.getElementsByClassName("playButton");
    for (const b of botons) {
        b.onclick = async () => {
            console.log("click");
            fetch("http://localhost:8080/run/" + b.id)

            getNewLogs()
        };
    }
};

async function getNewLogs() {
    const appLog = document.getElementById("log");
    const lastLogElement = document.getElementById("last_log");
    const lastLog = lastLogElement.textContent.trim();
    let logs = "No se pudieron descargar los logs";

    try {
        const response = await fetch("http://localhost:8080/getLastLogs/" + lastLog);
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        console.log(response.body)
        logs = await response.json(); // Obtener el texto de la respuesta
        appLog.textContent += logs.log

    } catch (error) {
        console.error(error);
    }

    lastLogElement.textContent = logs; // Actualizar el último registro
    return logs; // Devolver el texto de los logs
}


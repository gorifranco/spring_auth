window.onload = () => {
    const loader = document.getElementById("loader");

    const queryString = window.location.search;

    const searchParams = new URLSearchParams(queryString);

    const borrarParametro = searchParams.get('borrat');
    if (borrarParametro) {
        if (borrarParametro == true) {
            alert("Connexió eliminada amb èxit")
            window.location.reload()
        } else {
            alert("No s'ha pogut eliminar la connexió")
        }
    }



    const botons = document.getElementsByClassName("playButton");
    for (const b of botons) {
        b.onclick = async () => {
            console.log("click");
            await fetch("http://localhost:8080/run/" + b.id);
            await getNewLogs();
        };
    }

    const ancoresEliminar = document.getElementsByClassName("eliminar");
    Array.from(ancoresEliminar).forEach(element => {
        element.onclick = () => {
            if (confirm("Segur que vols eliminar la connexió?") == true) {
                fetch("eliminarPool/" + element.id)
            }
        }
    });
};

async function getNewLogs() {
    console.log("aqui")
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
        appLog.innerHTML += logs.log

    } catch (error) {
        console.error(error);
    }

    lastLogElement.textContent = logs; // Actualizar el último registro

}


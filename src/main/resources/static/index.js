window.onload = () => {
    const logs = document.getElementById("log")
    const loader = document.getElementById("loader")
    document.getElementById("run").onclick = () => {
        loader.style.display = "block"
        fetch("localhost:8080/run")
        .then(r => r.json())
        .then(r => {
            logs.innerHTML += getNewLogs();
        })
    }
}

async function getNewLogs() {
    const lastLog = document.getElementById("last_log").textContent
    let logs = "No s'han pogut descarregar els logs";

    try {
        const response = await fetch("http://localhost:8080/getLastLogs" + lastLog);
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        logs = await response.json();
    } catch (error) {
        console.error(error);
    }
    lastLog.textContent = logs.last_log
    return logs.log;
}
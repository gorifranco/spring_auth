window.onload = () => {

    const url = new URL(window.location.href);
    const searchParams = new URLSearchParams(url.search);

    if (searchParams.get('okey') === 'true') {
        alert('Configuració modificada correctament');
    }


    const ddbb_in_select = document.getElementById("ddbb_in_type")
    const ddbb_in_url = document.getElementById("ddbb_in_url")
    const ddbb_in_user = document.getElementById("ddbb_in_user")
    const ddbb_in_password = document.getElementById("ddbb_in_password")
    const ddbb_in_schema = document.getElementById("ddbb_in_schema")
    const ddbb_in_port = document.getElementById("ddbb_in_port")

    const ddbb_out_select = document.getElementById("ddbb_out_type")
    const ddbb_out_url = document.getElementById("ddbb_out_url")
    const ddbb_out_user = document.getElementById("ddbb_out_user")
    const ddbb_out_password = document.getElementById("ddbb_out_password")
    const ddbb_out_schema = document.getElementById("ddbb_out_schema")
    const ddbb_out_port = document.getElementById("ddbb_out_port")

    ddbb_in_select.addEventListener("change", () => {
        if(ddbb_in_select.value == "json"){
            ddbb_in_user.disabled = true
            ddbb_in_password.disabled = true
            ddbb_in_schema.disabled = true
            ddbb_in_port.disabled = true
        }else{
            ddbb_in_user.disabled = false
            ddbb_in_password.disabled = false
            ddbb_in_schema.disabled = false
            ddbb_in_port.disabled = false
        } 
    })

    ddbb_out_select.addEventListener("change", () => {
        if(ddbb_out_select.value == "json"){
            ddbb_out_user.disabled = true
            ddbb_out_password.disabled = true
            ddbb_out_schema.disabled = true
            ddbb_out_port.disabled = true
        }else{
            ddbb_out_user.disabled = false
            ddbb_out_password.disabled = false
            ddbb_out_schema.disabled = false
            ddbb_out_port.disabled = false
        } 
    })

    const periodic = document.getElementById("periodically_execution")
    const span = periodic.nextElementSibling
    const time_unit = document.getElementById("time_unit")
    const time_interval = document.getElementById("time_interval")

    periodic.addEventListener("change", () => {
        if(periodic.checked == true){
            span.innerText = "SI" 
            time_unit.disabled = false
            time_interval.disabled = false
        }else{
            span.innerText = "NO"
            time_unit.disabled = true
            time_interval.disabled = true
        } 
    })

    const mail = document.getElementById("send_mail")
    const span_mail = mail.nextElementSibling

    mail.addEventListener("change", () => {
        if(mail.checked == true){
            span_mail.innerText = "SI"
        }else{
            span_mail.innerText = "NO"
        }
    })
}
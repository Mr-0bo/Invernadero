const API_BASE = 'http://localhost:8080';
let controller;
let myChart = null;
let cacheHistorial = [];

function getLocalISOString(date) {
    const tzOffset = date.getTimezoneOffset() * 60000;
    const localISOTime = (new Date(date - tzOffset)).toISOString().slice(0, -1);
    return localISOTime.substring(0, 19);
}

// Navegación principal
function cambiarSeccion(id) {
    document.querySelectorAll('section').forEach(s => s.classList.add('seccion-oculta'));
    document.getElementById('sec-' + id).classList.remove('seccion-oculta');

    const botones = ['dashboard', 'historial', 'alertas', 'manual'];
    botones.forEach(btn => {
        const el = document.getElementById('btn-' + btn);
        if(el) el.className = "w-full flex items-center px-4 py-3 text-slate-300 hover:bg-slate-800 hover:text-white rounded-xl transition-all";
    });

    const btnActivo = document.getElementById('btn-' + id);
    if(btnActivo) btnActivo.className = "w-full flex items-center px-4 py-3 bg-emerald-500 text-white rounded-xl transition-all shadow-md";

    const titulos = { 'dashboard': 'Vista General', 'historial': 'Historial', 'alertas': 'Alertas', 'manual': 'Modo Manual' };
    document.getElementById('titulo-seccion').innerText = titulos[id];

    // Triggers automáticos al entrar a cada sección
    if(id === 'dashboard') {
        const idActual = document.getElementById('zonaSelect').value;
        if(idActual) obtenerDatosZona(idActual);
        actualizarContadorAlertas();
    } else if(id === 'historial') {
        obtenerHistorialCompleto();
    } else if(id === 'alertas') {
        cargarAlertasUI(); // <-- Disparamos la carga de alertas
    }
}

function limpiarUI() {
    document.getElementById('val-temp').innerText = "--";
    document.getElementById('val-hum').innerText = "--";
    document.getElementById('val-rad').innerText = "--";
    document.getElementById('val-vent').innerText = "--";

    const iconoVent = document.getElementById('icono-vent');
    iconoVent.className = "fa-solid fa-fan text-gray-300 mr-2";
    cerrarGrafica();
}

async function cargarZonas() {
    try {
        const res = await fetch(`${API_BASE}/api/zonas`);
        const zonas = await res.json();
        const sel = document.getElementById('zonaSelect');

        sel.innerHTML = zonas.map(z => `<option value="${z.idZona}">${z.nombre}</option>`).join('');

        if (zonas.length > 0) {
            sel.value = zonas[0].idZona;
            obtenerDatosZona(zonas[0].idZona);
        }
    } catch (e) { console.error("Error cargando zonas:", e); }
}

async function obtenerDatosZona(idZona) {
    if (controller) controller.abort();
    controller = new AbortController();
    const signal = controller.signal;

    limpiarUI();
    try {
        const res = await fetch(`${API_BASE}/api/registros/ultimo/${idZona}`, { signal });
        if (!res.ok) return;

        const data = await res.json();

        document.getElementById('val-temp').innerText = data.temperaturaExterior + " °C";
        document.getElementById('val-hum').innerText = data.humedadRelativa + " %";
        document.getElementById('val-rad').innerText = data.radiacionSolar;

        const textoVent = document.getElementById('val-vent');
        const iconoVent = document.getElementById('icono-vent');
        textoVent.innerText = data.estadoVentilacion;

        if (data.estadoVentilacion.includes("Encendido")) {
            iconoVent.className = "fa-solid fa-fan fa-spin text-emerald-500 mr-2";
        } else {
            iconoVent.className = "fa-solid fa-fan text-gray-400 mr-2";
        }
    } catch (e) {
        if (e.name !== 'AbortError') console.error(e);
    }
}

// --- HISTORIAL ---
async function obtenerHistorialCompleto() {
    try {
        const res = await fetch(`${API_BASE}/api/registros/todos`);
        if(res.ok) {
            cacheHistorial = await res.json();
            cacheHistorial.sort((a,b) => new Date(b.fechaHora) - new Date(a.fechaHora));
            renderizarTablaHistorial();
        }
    } catch (e) { console.error("Error cargando historial general:", e); }
}

function renderizarTablaHistorial() {
    const verNasa = document.getElementById('chk-nasa').checked;
    const verManual = document.getElementById('chk-manual').checked;
    const tbody = document.getElementById('tabla-historial-body');

    const filtrados = cacheHistorial.filter(reg => {
        const nombreZona = reg.zona && reg.zona.nombre ? reg.zona.nombre.toLowerCase() : '';
        if (nombreZona.includes('nasa') && verNasa) return true;
        if (nombreZona.includes('manual') && verManual) return true;
        return false;
    });

    if (filtrados.length === 0) {
        tbody.innerHTML = `<tr><td colspan="9" class="text-center py-8 text-gray-400 font-medium bg-gray-50">Ningún registro coincide con los filtros seleccionados.</td></tr>`;
        return;
    }

    tbody.innerHTML = filtrados.map(reg => {
        const fechaFormatted = new Date(reg.fechaHora).toLocaleString();
        const esNasa = reg.zona?.nombre?.toLowerCase().includes('nasa');

        const badgeZona = esNasa
            ? `<span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-semibold bg-blue-50 text-blue-700 border border-blue-200"><i class="fa-solid fa-satellite mr-1"></i> NASA</span>`
            : `<span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-semibold bg-purple-50 text-purple-700 border border-purple-200"><i class="fa-solid fa-keyboard mr-1"></i> Manual</span>`;

        return `
            <tr class="hover:bg-slate-50/80 transition-colors">
                <td class="px-5 py-3.5 font-medium text-gray-700 whitespace-nowrap">${fechaFormatted}</td>
                <td class="px-4 py-3.5">${badgeZona}</td>
                <td class="px-4 py-3.5 text-gray-600">${reg.temperaturaExterior} °C</td>
                <td class="px-4 py-3.5 text-gray-600">${reg.temperaturaInterior} °C</td>
                <td class="px-4 py-3.5 text-gray-600">${reg.humedadRelativa} %</td>
                <td class="px-4 py-3.5 text-gray-600">${reg.humedadSuelo} %</td>
                <td class="px-4 py-3.5 text-gray-600">${reg.radiacionSolar}</td>
                <td class="px-4 py-3.5 text-gray-600">${reg.indiceUv}</td>
                <td class="px-4 py-3.5 font-medium ${reg.estadoVentilacion.includes('Encendido') ? 'text-emerald-600' : 'text-slate-500'}">${reg.estadoVentilacion}</td>
            </tr>
        `;
    }).join('');
}

// --- GRÁFICAS DINÁMICAS ---
function cerrarGrafica() {
    document.getElementById('contenedor-grafica').classList.add('hidden');
}

async function mostrarGrafica(tipoMetrica) {
    const idZona = document.getElementById('zonaSelect').value;
    if (!idZona) return;

    const contenedor = document.getElementById('contenedor-grafica');
    contenedor.classList.remove('hidden');

    const finDate = new Date();
    const inicioDate = new Date(finDate.getTime() - (24 * 60 * 60 * 1000));

    const inicioStr = getLocalISOString(inicioDate);
    const finStr = getLocalISOString(finDate);

    try {
        const res = await fetch(`${API_BASE}/api/registros?idZona=${idZona}&inicio=${inicioStr}&fin=${finStr}`);
        if (!res.ok) throw new Error();

        let datos = await res.json();
        datos = datos.reverse();

        let labels = []; let values = []; let titulo = "";
        let colorBorde = ""; let colorFondo = ""; let unidad = "";

        if (tipoMetrica === 'temp') {
            titulo = "Temperatura Exterior"; unidad = "°C";
            colorBorde = "#f97316"; colorFondo = "rgba(249, 115, 22, 0.15)";
            values = datos.map(d => d.temperaturaExterior);
        } else if (tipoMetrica === 'hum') {
            titulo = "Humedad Relativa"; unidad = "%";
            colorBorde = "#3b82f6"; colorFondo = "rgba(59, 130, 246, 0.15)";
            values = datos.map(d => d.humedadRelativa);
        } else if (tipoMetrica === 'rad') {
            titulo = "Radiación Solar"; unidad = " W/m²";
            colorBorde = "#facc15"; colorFondo = "rgba(250, 204, 21, 0.15)";
            values = datos.map(d => d.radiacionSolar);
        }

        labels = datos.map(d => new Date(d.fechaHora).toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'}));

        if (values.length > 0) {
            const min = Math.min(...values).toFixed(2);
            const max = Math.max(...values).toFixed(2);
            const sum = values.reduce((a, b) => a + b, 0);
            const avg = (sum / values.length).toFixed(2);

            document.getElementById('stat-avg').innerText = `${avg} ${unidad}`;
            document.getElementById('stat-min').innerText = `${min} ${unidad}`;
            document.getElementById('stat-max').innerText = `${max} ${unidad}`;
        }

        document.getElementById('titulo-grafica').innerHTML = `<i class="fa-solid fa-chart-area mr-2"></i>Historial 24h: ${titulo}`;

        if (myChart) myChart.destroy();

        const ctx = document.getElementById('miGrafica').getContext('2d');
        myChart = new Chart(ctx, {
            type: 'line',
            data: {
                labels: labels,
                datasets: [{
                    label: titulo,
                    data: values,
                    borderColor: colorBorde,
                    backgroundColor: colorFondo,
                    borderWidth: 2.5,
                    pointRadius: 2,
                    fill: true,
                    tension: 0.35
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: { legend: { display: false } },
                scales: { x: { grid: { display: false } }, y: { grid: { color: '#f3f4f6' } } }
            }
        });
    } catch (error) {
        alert("No hay suficientes datos en las últimas 24 hrs para esta zona.");
        cerrarGrafica();
    }
}

// --- GESTIÓN DE ALERTAS (NUEVO) ---
async function actualizarContadorAlertas() {
    try {
        const res = await fetch(`${API_BASE}/api/alertas`);
        if (res.ok) {
            const alertas = await res.json();
            document.getElementById('val-alertas').innerText = alertas.length;
        } else {
            document.getElementById('val-alertas').innerText = "0";
        }
    } catch (e) { document.getElementById('val-alertas').innerText = "--"; }
}

async function cargarAlertasUI() {
    try {
        const res = await fetch(`${API_BASE}/api/alertas`);
        if (res.ok) {
            const alertas = await res.json();
            renderizarAlertas(alertas);
            document.getElementById('val-alertas').innerText = alertas.length;
        }
    } catch (e) { console.error("Error cargando alertas UI:", e); }
}

function renderizarAlertas(alertas) {
    const contenedor = document.getElementById('contenedor-alertas');

    // Si no hay alertas, mostramos un mensaje de "Todo OK"
    if (alertas.length === 0) {
        contenedor.innerHTML = `
            <div class="col-span-full py-12 px-6 text-center text-gray-500 bg-gray-50 rounded-xl border-2 border-dashed border-gray-200">
                <i class="fa-solid fa-shield-check text-5xl text-emerald-400 mb-4 block"></i>
                <h4 class="text-xl font-bold text-gray-700">Sistema Seguro</h4>
                <p class="text-sm mt-2">No hay anomalías activas en el invernadero en este momento.</p>
            </div>`;
        return;
    }

    // Dibujamos una tarjeta por cada alerta
    contenedor.innerHTML = alertas.map(alerta => {
        // Formateo de fecha y lógica de iconos
        const fecha = alerta.fechaHoraGenerada ? new Date(alerta.fechaHoraGenerada).toLocaleString() : 'Reciente';
        const esCalor = alerta.tipoAlerta.includes("CALOR") || alerta.tipoAlerta.includes("TÉRMICO");
        const colorIco = esCalor ? 'text-orange-500' : 'text-blue-500';
        const bgIco = esCalor ? 'bg-orange-50' : 'bg-blue-50';
        const icon = esCalor ? 'fa-temperature-arrow-up' : 'fa-droplet-slash';
        const zona = alerta.registroAmbiental?.zona?.nombre || 'Zona Desconocida';

        return `
            <div class="bg-white border border-red-200 rounded-xl p-5 shadow-sm hover:shadow-md transition-shadow relative overflow-hidden flex flex-col justify-between">
                <div class="absolute top-0 left-0 w-1.5 h-full bg-red-500"></div>
                
                <div>
                    <div class="flex justify-between items-start mb-4">
                        <div class="flex items-center space-x-3">
                            <div class="${bgIco} p-3 rounded-lg border border-red-50">
                                <i class="fa-solid ${icon} ${colorIco} text-xl"></i>
                            </div>
                            <div>
                                <h4 class="font-bold text-gray-800 tracking-tight">${alerta.tipoAlerta}</h4>
                                <p class="text-xs font-semibold text-gray-500 uppercase tracking-wider"><i class="fa-solid fa-location-dot mr-1"></i> ${zona}</p>
                            </div>
                        </div>
                        <span class="text-xs font-semibold text-gray-500 bg-gray-100 px-2.5 py-1 rounded-md border border-gray-200 shadow-sm">${fecha}</span>
                    </div>
                    
                    <div class="bg-slate-50 p-3 rounded-lg border border-slate-100 mb-5">
                        <p class="text-sm text-slate-600 leading-relaxed">${alerta.mensaje}</p>
                    </div>
                </div>

                <button onclick="resolverAlerta(${alerta.idAlerta})" class="w-full py-2.5 bg-slate-800 text-white text-sm font-bold rounded-lg hover:bg-emerald-500 hover:shadow-lg transition-all flex items-center justify-center transform active:scale-95 group">
                    <i class="fa-solid fa-check mr-2 group-hover:scale-125 transition-transform"></i> Marcar como Atendida
                </button>
            </div>
        `;
    }).join('');
}

async function resolverAlerta(idAlerta) {
    try {
        const res = await fetch(`${API_BASE}/api/alertas/${idAlerta}/resolver`, { method: 'PUT' });
        if (res.ok) {
            cargarAlertasUI(); // Refresca la lista y quita la tarjeta
        }
    } catch (e) { console.error("Error al resolver:", e); }
}

// --- FORMULARIO MANUAL ---
document.getElementById('formManual').addEventListener('submit', async (e) => {
    e.preventDefault();

    const idZonaManual = 4;
    const tempExt = parseFloat(document.getElementById('m-temp').value);
    const radSolar = parseFloat(document.getElementById('m-rad').value);

    const payload = {
        temperaturaExterior: tempExt,
        humedadRelativa: document.getElementById('m-hum').value,
        radiacionSolar: radSolar,
        temperaturaInterior: tempExt + 3.5,
        humedadSuelo: 45.0,
        indiceUv: 1.0,
        estadoVentilacion: (tempExt + 3.5 > 30) ? "Encendido 🟢" : "Apagado ⚪",
        zona: { idZona: idZonaManual }
    };

    try {
        const res = await fetch(`${API_BASE}/api/registros`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        if(res.ok) {
            alert("Datos inyectados en Zona Manual");
            document.getElementById('formManual').reset();
            document.getElementById('zonaSelect').value = idZonaManual;
            cambiarSeccion('dashboard');
            obtenerDatosZona(idZonaManual);
        }
    } catch (e) { console.error(e); }
});

// Eventos Extras
document.getElementById('chk-nasa').addEventListener('change', renderizarTablaHistorial);
document.getElementById('chk-manual').addEventListener('change', renderizarTablaHistorial);

document.getElementById('zonaSelect').addEventListener('change', (e) => {
    obtenerDatosZona(e.target.value);
    cerrarGrafica();
});

setInterval(() => document.getElementById('reloj').innerText = new Date().toLocaleTimeString(), 1000);

window.onload = () => {
    cargarZonas();
    actualizarContadorAlertas();
};
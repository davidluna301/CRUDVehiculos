const API_BASE = '/vehiculos-api/api/vehiculos';

let vehiculos = [];
let editingId = null;

// Elementos DOM
const elements = {
    form: document.getElementById('vehiculo-form'),
    tableBody: document.getElementById('vehiculos-body'),
    totalVehiculos: document.getElementById('total-vehiculos'),
    searchInput: document.getElementById('search-input'),
    filterTipo: document.getElementById('filter-tipo'),
    refreshBtn: document.getElementById('refresh-btn'),
    deleteModal: document.getElementById('delete-modal'),
    deleteMessage: document.getElementById('delete-message'),
    confirmDelete: document.getElementById('confirm-delete'),
    cancelDelete: document.getElementById('cancel-delete')
};

// Inicialización
document.addEventListener('DOMContentLoaded', function() {
    initApp();
});

function initApp() {
    loadVehiculos();
    setupEventListeners();
}

function setupEventListeners() {
    elements.form.addEventListener('submit', handleFormSubmit);
    elements.searchInput.addEventListener('input', filterVehiculos);
    elements.filterTipo.addEventListener('change', filterVehiculos);
    elements.refreshBtn.addEventListener('click', loadVehiculos);
    elements.confirmDelete.addEventListener('click', confirmDelete);
    elements.cancelDelete.addEventListener('click', closeModal);
}

// Cargar vehículos
async function loadVehiculos() {
    try {
        const response = await fetch(API_BASE);
        if (response.ok) {
            vehiculos = await response.json();
            renderTable(vehiculos);
            updateStats();
            showAlert('Datos cargados correctamente', 'success');
        } else {
            throw new Error('Error al cargar vehículos');
        }
    } catch (error) {
        showAlert('Error: ' + error.message, 'error');
    }
}

// Crear/Actualizar vehículo
async function handleFormSubmit(e) {
    e.preventDefault();
    
    const formData = new FormData(elements.form);
    const vehiculoData = {
        marca: formData.get('marca') || document.getElementById('marca').value,
        modelo: formData.get('modelo') || document.getElementById('modelo').value,
        matricula: formData.get('matricula') || document.getElementById('matricula').value,
        año: parseInt(document.getElementById('año').value),
        color: document.getElementById('color').value,
        precio: parseFloat(document.getElementById('precio').value),
        tipo: document.getElementById('tipo').value
    };

    try {
        if (editingId) {
            await updateVehiculo(editingId, vehiculoData);
            showAlert('Vehículo actualizado', 'success');
        } else {
            await createVehiculo(vehiculoData);
            showAlert('Vehículo creado', 'success');
        }
        resetForm();
        await loadVehiculos();
    } catch (error) {
        showAlert('Error: ' + error.message, 'error');
    }
}

async function createVehiculo(vehiculo) {
    const response = await fetch(API_BASE, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(vehiculo)
    });
    
    if (!response.ok) {
        const error = await response.json();
        throw new Error(error.error || 'Error al crear');
    }
    
    return await response.json();
}

async function updateVehiculo(id, vehiculo) {
    const response = await fetch(`${API_BASE}/${id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(vehiculo)
    });
    
    if (!response.ok) {
        const error = await response.json();
        throw new Error(error.error || 'Error al actualizar');
    }
    
    return await response.json();
}

async function deleteVehiculo(id) {
    const response = await fetch(`${API_BASE}/${id}`, {
        method: 'DELETE'
    });
    
    if (!response.ok) {
        throw new Error('Error al eliminar');
    }
}

// Renderizar tabla
function renderTable(data) {
    elements.tableBody.innerHTML = data.map(vehiculo => `
        <tr>
            <td>${vehiculo.marca}</td>
            <td>${vehiculo.modelo}</td>
            <td><strong>${vehiculo.matricula}</strong></td>
            <td>${vehiculo.año}</td>
            <td>${vehiculo.color}</td>
            <td>€${vehiculo.precio.toLocaleString()}</td>
            <td><span class="badge badge-${vehiculo.tipo.toLowerCase()}">${vehiculo.tipo}</span></td>
            <td class="actions">
                <button class="btn btn-success" onclick="editVehiculo('${vehiculo.id}')">
                    <i class="fas fa-edit"></i>
                </button>
                <button class="btn btn-danger" onclick="showDeleteModal('${vehiculo.id}', '${vehiculo.marca} ${vehiculo.modelo}')">
                    <i class="fas fa-trash"></i>
                </button>
            </td>
        </tr>
    `).join('');
}

// Editar vehículo
function editVehiculo(id) {
    const vehiculo = vehiculos.find(v => v.id === id);
    if (!vehiculo) return;
    
    editingId = id;
    document.getElementById('vehiculo-id').value = vehiculo.id;
    document.getElementById('marca').value = vehiculo.marca;
    document.getElementById('modelo').value = vehiculo.modelo;
    document.getElementById('matricula').value = vehiculo.matricula;
    document.getElementById('año').value = vehiculo.año;
    document.getElementById('color').value = vehiculo.color;
    document.getElementById('precio').value = vehiculo.precio;
    document.getElementById('tipo').value = vehiculo.tipo;
    
    document.getElementById('form-title').textContent = 'Editando Vehículo';
    document.getElementById('submit-btn').innerHTML = '<i class="fas fa-save"></i> Actualizar';
    document.getElementById('cancel-btn').style.display = 'inline-block';
}

// Eliminar vehículo
function showDeleteModal(id, info) {
    elements.deleteMessage.textContent = `¿Eliminar ${info}?`;
    elements.deleteModal.style.display = 'block';
    elements.confirmDelete.onclick = () => confirmDelete(id);
}

async function confirmDelete(id) {
    try {
        await deleteVehiculo(id);
        showAlert('Vehículo eliminado', 'success');
        closeModal();
        await loadVehiculos();
    } catch (error) {
        showAlert('Error al eliminar: ' + error.message, 'error');
        closeModal();
    }
}

function closeModal() {
    elements.deleteModal.style.display = 'none';
}

// Utilidades
function filterVehiculos() {
    const searchTerm = elements.searchInput.value.toLowerCase();
    const tipoFilter = elements.filterTipo.value;
    
    let filtered = vehiculos.filter(vehiculo => {
        const matchesSearch = 
            vehiculo.marca.toLowerCase().includes(searchTerm) ||
            vehiculo.modelo.toLowerCase().includes(searchTerm) ||
            vehiculo.matricula.toLowerCase().includes(searchTerm);
        
        const matchesTipo = !tipoFilter || vehiculo.tipo === tipoFilter;
        
        return matchesSearch && matchesTipo;
    });
    
    renderTable(filtered);
}

function resetForm() {
    elements.form.reset();
    editingId = null;
    document.getElementById('form-title').textContent = 'Agregar Vehículo';
    document.getElementById('submit-btn').innerHTML = '<i class="fas fa-save"></i> Guardar';
    document.getElementById('cancel-btn').style.display = 'none';
}

function updateStats() {
    elements.totalVehiculos.textContent = vehiculos.length;
}

function showAlert(message, type) {
    Swal.fire({
        icon: type,
        title: message,
        toast: true,
        position: 'top-end',
        showConfirmButton: false,
        timer: 3000
    });
}

// Hacer funciones globales
window.editVehiculo = editVehiculo;
window.showDeleteModal = showDeleteModal;
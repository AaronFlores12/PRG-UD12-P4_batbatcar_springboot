package es.batbatcar.v2p4.modelo.repositories;

import es.batbatcar.v2p4.exceptions.*;
import es.batbatcar.v2p4.modelo.dao.inmemorydao.InMemoryReservaDAO;
import es.batbatcar.v2p4.modelo.dao.inmemorydao.InMemoryViajeDAO;
import es.batbatcar.v2p4.modelo.dto.Reserva;
import es.batbatcar.v2p4.modelo.dto.viaje.EstadoViaje;
import es.batbatcar.v2p4.modelo.dto.viaje.Viaje;
import es.batbatcar.v2p4.modelo.dao.interfaces.ReservaDAO;
import es.batbatcar.v2p4.modelo.dao.interfaces.ViajeDAO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Repository
public class ViajesRepository {

    private final ViajeDAO viajeDAO;
    private final ReservaDAO reservaDAO;

    public ViajesRepository(@Autowired InMemoryViajeDAO viajeDAO, @Autowired InMemoryReservaDAO reservaDAO) {
        this.viajeDAO = viajeDAO;
        this.reservaDAO = reservaDAO;
    }

	public Viaje findAll(String codViaje) throws ViajeNotFoundException {
		Viaje viaje = viajeDAO.findById(Integer.parseInt(codViaje));
		if (viaje == null ){
			throw new ViajeNotFoundException(codViaje);
		}
		return viaje;
	}
    
    /** 
     * Obtiene un conjunto de todos los viajes
     * @return
     */
    public Set<Viaje> findAll() {
        
    	// Se recuperan todos los viajes del DAO de viajes
    	Set<Viaje> viajes = viajeDAO.findAll();
        
    	// Se completa la información acerca de las reservas de cada viaje a través del DAO de reservas
        for (Viaje viaje : viajes) {
        	if (this.reservaDAO.findAllByTravel(viaje).size() > 0) {
            	viaje.setSeHanRealizadoReservas(true);
            }
		}
        return viajes;
    }
    
    /**
     * Obtiene el código del siguiente viaje
     * @return
     */
    public int getNextCodViaje() {
        return this.viajeDAO.findAll().size() + 1;
    }

	public int getNextCodigoReserva(Viaje viaje) {
		return this.reservaDAO.findAllByTravel(viaje).size() + 1;
	}
    
    /**
     * Guarda el viaje (actualiza si ya existe o añade si no existe)
     * @param viaje
     * @throws ViajeAlreadyExistsException
     * @throws ViajeNotFoundException
     */
    public void save(Viaje viaje) throws ViajeAlreadyExistsException, ViajeNotFoundException {
    	
    	if (viajeDAO.findById(viaje.getCodViaje()) == null) {
    		viajeDAO.add(viaje);
    	} else {
    		viajeDAO.update(viaje);
    	}
    }
	
    /**
     * Encuentra todas las reservas de @viaje
     * @param viaje
     * @return
     */
	public List<Reserva> findReservasByViaje(Viaje viaje) {
		return reservaDAO.findAllByTravel(viaje);
	}
	
	/**
	 * Guarda la reserva
	 * @param reserva
	 * @throws ReservaAlreadyExistsException
	 * @throws ReservaNotFoundException
	 */
    public void save(Reserva reserva) throws ReservaAlreadyExistsException, ReservaNotFoundException {
    	
    	if (reservaDAO.findById(reserva.getCodigoReserva()) == null) {
    		reservaDAO.add(reserva);
    	} else {
    		reservaDAO.update(reserva);
    	}
    }
    
    /**
     * Elimina la reserva
     * @param reserva
     * @throws ReservaNotFoundException
     */
	public void remove(Reserva reserva) throws ReservaNotFoundException {
		reservaDAO.remove(reserva);
	}

	public Set<Viaje> buscarPorLugarDestino(String destino) throws ViajeNotFoundException {
		Set<Viaje> viajes = viajeDAO.findAll(destino);
		if (viajes.isEmpty()) {
			throw new ViajeNotFoundException(destino);
		}
		return viajes;
	}

	public Viaje findViajeSipermiteReserva(int codViaje, String usuario, int plazasSolicitadas ) throws ReservaNoValidaException {
		Viaje viaje = viajeDAO.findById(codViaje);
		if (viaje.getPropietario().equals(usuario)){
			throw new ReservaNoValidaException("El propietario no puede ser igual al usuario");
		} else if (!viaje.tieneEsteEstado(EstadoViaje.ABIERTO)){
			throw new ReservaNoValidaException("El viaje no esta disponible");
		} else if (viaje.isCancelado()){
			throw new ReservaNoValidaException("El viaje esta cancelado");
		} else if (viaje.getPlazasOfertadas()<plazasSolicitadas){
			throw new ReservaNoValidaException("Las plazas no pueden ser mayores a las ofertadas");
		} else if (findReservasByViaje(viaje).size()>plazasSolicitadas){
			throw new ReservaNoValidaException("Las plazas solicitadas no pueden ser mayores a las reservas");
		} else {
			List <Reserva> reservas = findReservasByViaje(viaje);
			for (Reserva reserva : reservas) {
				if (reserva.getUsuario().equals(usuario)){
					throw new ReservaNoValidaException("El usuario ya ha hecho una reserva en este viaje");
				}
			}
		}
		return viaje;
	}

}

package es.batbatcar.v2p4.modelo.dao.sqldao;

import es.batbatcar.v2p4.exceptions.ReservaAlreadyExistsException;
import es.batbatcar.v2p4.exceptions.ReservaNotFoundException;
import es.batbatcar.v2p4.modelo.dto.Reserva;
import es.batbatcar.v2p4.modelo.dto.viaje.Viaje;
import es.batbatcar.v2p4.modelo.services.MySQLConnection;
import es.batbatcar.v2p4.modelo.dao.interfaces.ReservaDAO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class SQLReservaDAO implements ReservaDAO {

	@Autowired
	private MySQLConnection mySQLConnection;

	@Override
	public Set<Reserva> findAll() {
		Connection connection = mySQLConnection.getConnection();
		String sql = String.format("SELECT * FROM reservas");
		Set<Reserva> reservas = new HashSet<>();

		try (
				Statement statement = connection.createStatement();
				ResultSet rs = statement.executeQuery(sql);
		) {
			while (rs.next()) {
				String codigoReserva = rs.getString("codigoReserva");
				String usuario = rs.getString("usuario");
				int plazasSolicitadas = rs.getInt("plazasSolicitadas");
				LocalDateTime fechaRealizacion = rs.getTimestamp("fechaRealizacion").toLocalDateTime();
				int codViaje = rs.getInt("codViaje");

				Viaje viaje = new Viaje(codViaje);
				Reserva reserva = new Reserva(codigoReserva, usuario, plazasSolicitadas, fechaRealizacion, viaje);
				reservas.add(reserva);
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return reservas;
	}

	@Override
	public Reserva findById(String id) {
		Connection connection = mySQLConnection.getConnection();
		String sql = String.format("SELECT * FROM reservas WHERE codigoReserva = ?");
		Reserva reserva = null;

		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, id);
			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				String codigoReserva = rs.getString("codigoReserva");
				String usuario = rs.getString("usuario");
				int plazasSolicitadas = rs.getInt("plazasSolicitadas");
				LocalDateTime fechaRealizacion = rs.getTimestamp("fechaRealizacion").toLocalDateTime();
				int codViaje = rs.getInt("codViaje");

				Viaje viaje = new Viaje(codViaje);
				reserva = new Reserva(codigoReserva, usuario, plazasSolicitadas, fechaRealizacion, viaje);
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return reserva;
	}

	@Override
	public ArrayList<Reserva> findAllByUser(String user) {
		throw new RuntimeException("Not yet implemented");
	}

	@Override
	public ArrayList<Reserva> findAllByTravel(Viaje viaje) {
		Connection connection = mySQLConnection.getConnection();
		String sql = String.format("SELECT * FROM reservas WHERE viaje = ?");
		ArrayList<Reserva> reservas = new ArrayList<>();

		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, viaje.getCodViaje());
			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				String codigoReserva = rs.getString("codigoReserva");
				String usuario = rs.getString("usuario");
				int plazasSolicitadas = rs.getInt("plazasSolicitadas");
				LocalDateTime fechaRealizacion = rs.getTimestamp("fechaRealizacion").toLocalDateTime();

				Reserva reserva = new Reserva(codigoReserva, usuario, plazasSolicitadas, fechaRealizacion, viaje);
				reservas.add(reserva);
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return reservas;
	}

	@Override
	public Reserva getById(String id) throws ReservaNotFoundException {
		throw new RuntimeException("Not yet implemented");
	}

	@Override
	public List<Reserva> findAllBySearchParams(Viaje viaje, String searchParams) {
		throw new RuntimeException("Not yet implemented");
	}

	@Override
	public void add(Reserva reserva) throws ReservaAlreadyExistsException {
		Connection connection = mySQLConnection.getConnection();
		String sql = String.format("INSERT INTO reservas (codigoReserva, usuario, plazasSolicitadas, fechaRealizacion, viaje) " +
				"VALUES (?, ?, ?, ?, ?)");

		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, reserva.getCodigoReserva());
			statement.setString(2, reserva.getUsuario());
			statement.setInt(3, reserva.getPlazasSolicitadas());
			statement.setTimestamp(4, Timestamp.valueOf(reserva.getFechaRealizacion()));
			statement.setInt(5, reserva.getViaje().getCodViaje());
			statement.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void update(Reserva reserva) throws ReservaNotFoundException {
		Connection connection = mySQLConnection.getConnection();
		String sql = String.format("UPDATE reservas SET usuario = ?, plazasSolicitadas = ?, fechaRealizacion = ?, viaje = ? " +
				"WHERE codigoReserva = ?");

		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, reserva.getUsuario());
			statement.setInt(2, reserva.getPlazasSolicitadas());
			statement.setTimestamp(3, Timestamp.valueOf(reserva.getFechaRealizacion()));
			statement.setInt(4, reserva.getViaje().getCodViaje());
			statement.setString(5, reserva.getCodigoReserva());

			int filasAtualizadas = statement.executeUpdate();
			if (filasAtualizadas == 0) {
				throw new ReservaNotFoundException(reserva.getCodigoReserva());
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	public void remove(Reserva reserva) throws ReservaNotFoundException {
		Connection connection = mySQLConnection.getConnection();
		String sql = String.format("DELETE FROM reservas WHERE codigoReserva = ?");

		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, reserva.getCodigoReserva());

			int filasEliminadas = statement.executeUpdate();
			if (filasEliminadas == 0) {
				throw new ReservaNotFoundException(reserva.getCodigoReserva());
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	public int getNumPlazasReservadasEnViaje(Viaje viaje) {
		throw new RuntimeException("Not yet implemented");
	}

	@Override
	public Reserva findByUserInTravel(String usuario, Viaje viaje) {
		throw new RuntimeException("Not yet implemented");
	}
}
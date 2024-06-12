package es.batbatcar.v2p4.modelo.dao.sqldao;


import es.batbatcar.v2p4.exceptions.ViajeNotCancelableException;
import es.batbatcar.v2p4.exceptions.ViajeNotFoundException;
import es.batbatcar.v2p4.modelo.services.MySQLConnection;
import es.batbatcar.v2p4.modelo.dao.interfaces.ViajeDAO;
import es.batbatcar.v2p4.modelo.dto.viaje.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Repository
public class SQLViajeDAO implements ViajeDAO {

    @Autowired
    private MySQLConnection mySQLConnection;

    @Override
    public Set<Viaje> findAll() {

        Connection connection = mySQLConnection.getConnection();
        String sql = String.format("SELECT * FROM viajes");
        Set<Viaje> viajes = new HashSet<>();

        try (
                Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery(sql);
        ) {
            while (rs.next()) {
                int codViaje = rs.getInt("codViaje");
                String propietario = rs.getString("propietario");
                String ruta = rs.getString("ruta");
                LocalDateTime fechaSalida = rs.getTimestamp("fechaSalida").toLocalDateTime();
                long duracion = rs.getLong("duracion");
                float precio = rs.getFloat("precio");
                int plazasOfertadas = rs.getInt("plazasOfertadas");
                EstadoViaje estadoViaje = EstadoViaje.ABIERTO;

                Viaje viaje = new Viaje(codViaje, propietario, ruta, fechaSalida, duracion, precio, plazasOfertadas, estadoViaje);
                viajes.add(viaje);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return viajes;
    }

    @Override
    public Set<Viaje> findAll(String city) {
        Connection connection = mySQLConnection.getConnection();
        String sql = String.format("SELECT * FROM viajes WHERE ruta LIKE ?");
        Set<Viaje> viajes = new HashSet<>();

        try (
                PreparedStatement statement = connection.prepareStatement(sql);
        ) {
            statement.setString(1, city);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                int codViaje = rs.getInt("codViaje");
                String propietario = rs.getString("propietario");
                String ruta = rs.getString("ruta");
                LocalDateTime fechaSalida = rs.getTimestamp("fechaSalida").toLocalDateTime();
                long duracion = rs.getLong("duracion");
                float precio = rs.getFloat("precio");
                int plazasOfertadas = rs.getInt("plazasOfertadas");
                EstadoViaje estadoViaje = EstadoViaje.valueOf(rs.getString("estadoViaje"));

                Viaje viaje = new Viaje(codViaje, propietario, ruta, fechaSalida, duracion, precio, plazasOfertadas, estadoViaje);
                viajes.add(viaje);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return viajes;
    }

    @Override
    public Set<Viaje> findAll(EstadoViaje estadoViaje) {
        Connection connection = mySQLConnection.getConnection();
        String sql = String.format("SELECT * FROM viajes WHERE estadoViaje = ?");
        Set<Viaje> viajes = new HashSet<>();

        try (
                PreparedStatement statement = connection.prepareStatement(sql);
        ) {
            statement.setString(1, estadoViaje.name());
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                int codViaje = rs.getInt("codViaje");
                String propietario = rs.getString("propietario");
                String ruta = rs.getString("ruta");
                LocalDateTime fechaSalida = rs.getTimestamp("fechaSalida").toLocalDateTime();
                long duracion = rs.getLong("duracion");
                float precio = rs.getFloat("precio");
                int plazasOfertadas = rs.getInt("plazasOfertadas");

                Viaje viaje = new Viaje(codViaje, propietario, ruta, fechaSalida, duracion, precio, plazasOfertadas, estadoViaje);
                viajes.add(viaje);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return viajes;
    }

    @Override
    public Set<Viaje> findAll(Class<? extends Viaje> viajeClass) {
        throw new RuntimeException("Not yet implemented");
    }

    @Override
    public Viaje findById(int codViaje) {
        Connection connection = mySQLConnection.getConnection();
        String sql = String.format("SELECT * FROM viajes WHERE codViaje = ?");
        Viaje viaje = null;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, codViaje);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                String propietario = rs.getString("propietario");
                String ruta = rs.getString("ruta");
                LocalDateTime fechaSalida = rs.getTimestamp("fechaSalida").toLocalDateTime();
                long duracion = rs.getLong("duracion");
                float precio = rs.getFloat("precio");
                int plazasOfertadas = rs.getInt("plazasOfertadas");
                EstadoViaje estadoViaje = EstadoViaje.valueOf(rs.getString("estadoViaje"));

                viaje = new Viaje(codViaje, propietario, ruta, fechaSalida, duracion, precio, plazasOfertadas, estadoViaje);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
        return viaje;
    }

    @Override
    public Viaje getById(int codViaje) throws ViajeNotFoundException {
        throw new RuntimeException("Not yet implemented");
    }

    @Override
    public void add(Viaje viaje) {
        Connection connection = mySQLConnection.getConnection();
        String sql = String.format("INSERT INTO viajes (codViaje, propietario, ruta, fechaSalida, duracion, precio, plazasOfertadas, estadoViaje) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)");

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, viaje.getCodViaje());
            statement.setString(2, viaje.getPropietario());
            statement.setString(3, viaje.getRuta());
            statement.setTimestamp(4, Timestamp.valueOf(viaje.getFechaSalida()));
            statement.setLong(5, viaje.getDuracion());
            statement.setFloat(6, viaje.getPrecio());
            statement.setInt(7, viaje.getPlazasOfertadas());
            statement.setString(8, String.valueOf(viaje.getEstado()));

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void update(Viaje viaje) throws ViajeNotFoundException {
        Connection connection = mySQLConnection.getConnection();
        String sql = String.format("UPDATE viajes SET propietario = ?, ruta = ?, fechaSalida = ?, duracion = ?, precio = ?, plazasOfertadas = ?, estadoViaje = ? WHERE codViaje = ?");

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, viaje.getPropietario());
            statement.setString(2, viaje.getRuta());
            statement.setTimestamp(3, Timestamp.valueOf(viaje.getFechaSalida()));
            statement.setLong(4, viaje.getDuracion());
            statement.setFloat(5, viaje.getPrecio());
            statement.setInt(6, viaje.getPlazasOfertadas());
            statement.setString(7, viaje.getEstado().name());
            statement.setInt(8, viaje.getCodViaje());

            int filasActualizadas = statement.executeUpdate();
            if (filasActualizadas == 0) {
                throw new ViajeNotFoundException("No se ha encontrado el viaje");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void remove(Viaje viaje) throws ViajeNotFoundException {
        Connection connection = mySQLConnection.getConnection();
        String sql = String.format("UPDATE viajes SET estadoViaje = ? WHERE codViaje = ?");

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, EstadoViaje.CANCELADO.name());
            statement.setInt(2, viaje.getCodViaje());

            int filasActualizadas = statement.executeUpdate();
            if (filasActualizadas == 0) {
                throw new ViajeNotFoundException("No se ha encontrado el viaje");
            }
            viaje.cancelar();
        } catch (SQLException | ViajeNotCancelableException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
package es.batbatcar.v2p4.controllers;

import es.batbatcar.v2p4.exceptions.ViajeAlreadyExistsException;
import es.batbatcar.v2p4.exceptions.ViajeNotFoundException;
import es.batbatcar.v2p4.modelo.dto.viaje.Viaje;
import es.batbatcar.v2p4.modelo.repositories.ViajesRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@Controller
public class ViajesController {

    @Autowired
    private ViajesRepository viajesRepository;
    
    /**
     * Endpoint que muestra el listado de todos los viajes disponibles
     *
     * */
    @GetMapping("viajes")
    public String getViajesAction(@RequestParam Map<String, String> params, Model model) throws ViajeNotFoundException {
        String ruta = params.get("ruta");
        if (ruta != null) {
            String[] destinos = ruta.split("-");
            String destinoFinal = destinos[destinos.length - 1];
            model.addAttribute("viajes", viajesRepository.buscarPorLugarDestino(destinoFinal));
            model.addAttribute("titulo", "Listado de viajes");
            return "viaje/listado";
        }
        model.addAttribute("viajes", viajesRepository.findAll());
        model.addAttribute("titulo", "Listado de viajes");
        return "viaje/listado";
    }

    @GetMapping("/viaje/add")
    public String viaje() {
        return "viaje/viaje_form";
    }


    @PostMapping(value = "/viaje/add")
    public String addViaje(@RequestParam Map<String, String> params, Model model, RedirectAttributes redirectAttributes) {
        try{
            int codViaje = viajesRepository.getNextCodViaje();
            String propietario = params.get("propietario");
            String ruta = params.get("ruta");
            LocalDate diaSalida = LocalDate.parse(params.get("diaSalida"));
            LocalTime horaSalida = LocalTime.parse(params.get("horaSalida"));
            LocalDateTime fechaSalida = LocalDateTime.of(diaSalida, horaSalida);
            long duracion = Long.parseLong(params.get("duracion"));
            float precio = Float.parseFloat(params.get("precio"));
            int plazasOfertadas = Integer.parseInt(params.get("plazasOfertadas"));
            Viaje viaje = new Viaje(codViaje,propietario,ruta,fechaSalida,duracion,precio,plazasOfertadas);
            viajesRepository.save(viaje);
            redirectAttributes.addFlashAttribute("infoMessage", "Viaje agregado con exito");
            return "redirect:/viajes";
        } catch (ViajeAlreadyExistsException | ViajeNotFoundException e) {
            HashMap<String, String> errors = new HashMap<>();
            errors.put("error: ","El viaje ya existe" + e.getMessage());
            redirectAttributes.addFlashAttribute("errors", errors);
            return "redirect:/viajes";
        }
    }

    @GetMapping("/viaje")
    public String verViaje(@RequestParam Map<String, String> params, Model model) throws ViajeNotFoundException {
        try{
            String codViaje = params.get("codViaje");
            model.addAttribute("viaje",viajesRepository.findAll(codViaje));
            return "/viaje/viaje_detalle";
        } catch (ViajeNotFoundException e) {
            HashMap<String, String> errors = new HashMap<>();
            errors.put("error: ","El viaje no existe" + e.getMessage());
            model.addAttribute("errors", errors);
            return "redirect:/viajes";
        }
    }

}

package es.batbatcar.v2p4.controllers;

import es.batbatcar.v2p4.exceptions.ReservaAlreadyExistsException;
import es.batbatcar.v2p4.exceptions.ReservaNotFoundException;
import es.batbatcar.v2p4.exceptions.ViajeAlreadyExistsException;
import es.batbatcar.v2p4.exceptions.ViajeNotFoundException;
import es.batbatcar.v2p4.modelo.dto.Reserva;
import es.batbatcar.v2p4.modelo.dto.viaje.Viaje;
import es.batbatcar.v2p4.modelo.repositories.ViajesRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Controller
public class ReservaController {

    @Autowired
    private ViajesRepository viajesRepository;

    @GetMapping("/viaje/reserva/add")
    public String reserva(@RequestParam Map<String, String> params, Model model) {
        String codViaje = params.get("codViaje");
        if (codViaje == null) {
            return "redirect:/viajes";
        }
        model.addAttribute("codViaje", codViaje);
        return "reserva/reserva_form";
    }

    @PostMapping(value = "viaje/reserva/add")
    public String addReserva(@RequestParam Map<String, String> params, Model model, RedirectAttributes redirectAttributes) {
        String codViaje = params.get("codViaje");
        try {
            String usuario = params.get("usuario");
            String plazas = params.get("plazasSolicitadas");
            int plazasSolicitadas = Integer.parseInt(plazas);
            Viaje viaje = viajesRepository.findAll(codViaje);
            Reserva reserva = new Reserva(codViaje, usuario, plazasSolicitadas, viaje);
            viajesRepository.save(reserva);
            redirectAttributes.addFlashAttribute("infoMessage", "Reserva agregada con exito");
            return "redirect:/viajes";
        } catch (ReservaNotFoundException | ReservaAlreadyExistsException | ViajeNotFoundException e) {
            HashMap<String, String> errors = new HashMap<>();
            errors.put("error: ","La reserva no se ha podido realizar" + e.getMessage());
            redirectAttributes.addFlashAttribute("errors", errors);
            model.addAttribute("codViaje", codViaje);
            return "redirect:/viaje/reserva/add";
        }
    }

}

package es.uji.ei1027.majorsacasa.controller;

import ch.qos.logback.core.net.SyslogOutputStream;
import es.uji.ei1027.majorsacasa.dao.AvailabilityDao;
import es.uji.ei1027.majorsacasa.dao.VolunteerDao;
import es.uji.ei1027.majorsacasa.model.Availability;
import es.uji.ei1027.majorsacasa.model.Volunteer;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/availability")
public class AvailabilityController {

    private AvailabilityDao availabilityDao;
    private VolunteerDao volunteerDao;

    @Autowired
    public void setAvailabilityDao(AvailabilityDao availabilityDao) {
        this.availabilityDao = availabilityDao;
    }
    
    @Autowired
    public void setVolunteerDao(VolunteerDao volunteerDao) {
        this.volunteerDao = volunteerDao;
    }
    
    @Autowired
    public void AvailabilityDao(AvailabilityDao availabilityDao) {
        this.availabilityDao = availabilityDao;
    }

    // Operacions: Crear, llistar, actualitzar, esborrar
    // ...
    @RequestMapping("/list")
    public String listAvailabilities(Model model) {
        model.addAttribute("availabilities", availabilityDao.getAvailabilities());
        return "availability/list";
    }

    @RequestMapping(value = "/add")
    public String addAvailability(Model model) {
        model.addAttribute("availability", new Availability());
        return "availability/add";
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String processAddSubmit(@ModelAttribute("availability") Availability availability, Model model, HttpSession session,
                                   BindingResult bindingResult) {
        Volunteer volunteer = (Volunteer) session.getAttribute("volunteer");
        availability.setDni_volunteer(volunteer.getDni());
        availability.setStateAvailability(true);

        AvailabilityValidator availabilityValidator = new AvailabilityValidator();
        availabilityValidator.validate(availability, bindingResult);
        List<Availability> horarios = availabilityDao.getAvailabilitiesVolunteer(volunteer);
        for(Availability horario : horarios){
            //Compruebo si las claves primarias de la nueva disponibilidad(dni_vol, fecha y hora_inicio) coinciden con alguna ya creada.
            if(horario.getDni_volunteer().equals(availability.getDni_volunteer()) && horario.getFecha().equals(availability.getFecha()) && horario.getBeginingHour().equals(availability.getBeginingHour())){
                bindingResult.rejectValue("beginingHour", "obligatori", "Ja existeix una disponibilitat amb aquestes dades");
            }
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("availabilities", availabilityDao.getAvailabilitiesVolunteer(volunteer));
            return "volunteer/horaris";
        }

        availabilityDao.addAvailability(availability, volunteer);
        return "redirect:/volunteer/horaris";
    }

    @RequestMapping(value = "/update/{fecha}/{dni_volunteer}/{beginingHour}", method = RequestMethod.GET)
    public String editaAvailability(Model model, @PathVariable String fecha,
                                    @PathVariable String dni_volunteer, @PathVariable String beginingHour) {
        LocalDate fecha1 = LocalDate.parse(fecha);
        LocalTime tiempo = LocalTime.parse(beginingHour);
        model.addAttribute("availability_update", availabilityDao.getAvailability(fecha1, dni_volunteer, tiempo));
        return "availability/update";
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public String processUpdateSubmit(
            @ModelAttribute("availability") Availability availability, HttpSession session,
            BindingResult bindingResult) {
        AvailabilityValidator availabilityValidador = new AvailabilityValidator();
        availabilityValidador.validate(availability, bindingResult);
        Volunteer volunteer = (Volunteer) session.getAttribute("volunteer");
        if (bindingResult.hasErrors())
            return "availability/update";
        availabilityDao.updateAvailability(availability, volunteer);
        return "redirect:list";
    }

    @RequestMapping(value = "/delete/{fecha}/{dni_volunteer}/{beginingHour}")
    public String processDelete(@PathVariable String fecha,
                                @PathVariable String dni_volunteer, @PathVariable String beginingHour) {
        LocalDate fecha1 = LocalDate.parse(fecha);
        LocalTime tiempo = LocalTime.parse(beginingHour);
        availabilityDao.deleteAvailability(fecha1, dni_volunteer, tiempo);
        return "redirect:/volunteer/horaris";
    }

    @RequestMapping(value = "/donaDeAltaAvailability/{dni_volunteer}/{fecha}/{beginingHour}/{endingHour}/{dni_elderly}", method = RequestMethod.GET)
    public String donaDeAltaAvailability(Model model, @PathVariable String fecha, @PathVariable String dni_volunteer, @PathVariable String beginingHour, @PathVariable String endingHour, @PathVariable String dni_elderly) {
        LocalDate fecha1 = LocalDate.parse(fecha);
        LocalTime tiempo_ini = LocalTime.parse(beginingHour);
        LocalTime tiempo_fin = LocalTime.parse(endingHour);
        Availability availability = new Availability(fecha1, tiempo_ini, tiempo_fin, false, dni_volunteer, dni_elderly);
        availabilityDao.donaDeAltaAvailability(availability);
        model.addAttribute("availabilities", availabilityDao.getAvailabilities());
        return "redirect:/availability/acompanyament";
    }
    
    @RequestMapping("/acompanyament")
    public String acompanyament(HttpSession session, Model model) {
    	model.addAttribute("volunteers", volunteerDao.getVolunteers());
    	model.addAttribute("availabilities", availabilityDao.getAvailabilities());
        return "elderly/acompanyament";
    }

}

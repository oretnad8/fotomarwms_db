package com.fotomar.ubicacionesservice.config;

import com.fotomar.ubicacionesservice.model.Ubicacion;
import com.fotomar.ubicacionesservice.repository.UbicacionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {
    
    private final UbicacionRepository ubicacionRepository;
    
    @Override
    public void run(String... args) {
        if (ubicacionRepository.count() == 0) {
            log.info("Inicializando ubicaciones...");
            
            char[] pisos = {'A', 'B', 'C'};
            
            for (char piso : pisos) {
                for (int numero = 1; numero <= 60; numero++) {
                    String codigoUbicacion = String.format("%c-%02d", piso, numero);
                    
                    if (!ubicacionRepository.existsByCodigoUbicacion(codigoUbicacion)) {
                        Ubicacion ubicacion = new Ubicacion();
                        ubicacion.setCodigoUbicacion(codigoUbicacion);
                        ubicacion.setPiso(piso);
                        ubicacion.setNumero(numero);
                        ubicacionRepository.save(ubicacion);
                    }
                }
            }
            
            log.info("180 ubicaciones creadas exitosamente (A-01 a C-60)");
        } else {
            log.info("Las ubicaciones ya están inicializadas");
        }
    }
}
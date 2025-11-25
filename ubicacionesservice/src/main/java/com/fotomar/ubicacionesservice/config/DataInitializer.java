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
            log.info("Inicializando ubicaciones del sistema de pasillos...");
            
            int pasillos = 5;        // P1 a P5
            int posiciones = 60;     // 01 a 60
            char[] pisos = {'A', 'B', 'C'};
            
            int contador = 0;
            
            for (int pasillo = 1; pasillo <= pasillos; pasillo++) {
                for (int numero = 1; numero <= posiciones; numero++) {
                    for (char piso : pisos) {
                        String codigoUbicacion = String.format("P%d-%c-%02d", pasillo, piso, numero);
                        
                        if (!ubicacionRepository.existsByCodigoUbicacion(codigoUbicacion)) {
                            Ubicacion ubicacion = new Ubicacion();
                            ubicacion.setCodigoUbicacion(codigoUbicacion);
                            ubicacion.setPasillo(pasillo);
                            ubicacion.setPiso(piso);
                            ubicacion.setNumero(numero);
                            ubicacionRepository.save(ubicacion);
                            contador++;
                        }
                    }
                }
            }
            
            log.info("✅ {} ubicaciones creadas exitosamente", contador);
            log.info("   - 5 pasillos (P1 a P5)");
            log.info("   - 60 posiciones por pasillo (01-60)");
            log.info("   - 3 pisos por posición (A, B, C)");
            log.info("   - Total: 900 ubicaciones (P1-A-01 a P5-C-60)");
        } else {
            log.info("Las ubicaciones ya están inicializadas ({} ubicaciones)", 
                    ubicacionRepository.count());
        }
    }
}
package com.patronSingleton;

import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.Scanner;

/**
 * Demo of the Singleton pattern: Screen Capture Service.
 * Only one instance of the service exists; all captures use the same instance.
 */
public class CasoSingleton {

    private static final String MENU = """
            --- Servicio de Captura de Pantalla (Singleton) ---
            1. Capturar pantalla y guardar
            2. Capturar pantalla (solo en memoria)
            3. Ver número de capturas realizadas
            4. Salir
            Elija una opción (1-4):\s""";

    public static void main(String[] args) {
        if (!isGraphicalEnvironment()) {
            System.out.println("Este programa requiere un entorno gráfico para capturar la pantalla.");
            System.out.println("Ejecútelo en un escritorio con pantalla disponible.");
            return;
        }

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print(MENU);
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1" -> doCaptureAndSave(scanner);
                case "2" -> doCaptureInMemory();
                case "3" -> showCaptureCount();
                case "4" -> {
                    System.out.println("Hasta luego.");
                    scanner.close();
                    return;
                }
                default -> System.out.println("Opción no válida. Introduzca 1, 2, 3 o 4.");
            }
        }
    }

    private static boolean isGraphicalEnvironment() {
        try {
            return !java.awt.GraphicsEnvironment.isHeadless();
        } catch (Exception e) {
            return false;
        }
    }

    private static void doCaptureAndSave(Scanner scanner) {
        ScreenCaptureService service = ScreenCaptureService.getInstance();

        System.out.println("¿Guardar en directorio personal por defecto? (S/n): ");
        String answer = scanner.nextLine().trim().toLowerCase();
        String directory = null;
        if (answer.equals("n") || answer.equals("no")) {
            System.out.print("Introduzca la ruta del directorio: ");
            directory = scanner.nextLine().trim();
            if (directory.isBlank()) {
                directory = null;
            }
        }

        System.out.println("Capturando pantalla...");
        Path savedPath = service.captureAndSave(directory);

        if (savedPath != null) {
            System.out.println("Captura guardada correctamente en: " + savedPath.toAbsolutePath());
        } else {
            System.out.println("Error: no se pudo guardar la captura.");
        }
    }

    private static void doCaptureInMemory() {
        ScreenCaptureService service = ScreenCaptureService.getInstance();
        BufferedImage image = service.captureFullScreen();
        if (image != null) {
            System.out.println("Pantalla capturada en memoria. Dimensiones: "
                    + image.getWidth() + " x " + image.getHeight() + " píxeles.");
        } else {
            System.out.println("Error al capturar la pantalla.");
        }
    }

    private static void showCaptureCount() {
        ScreenCaptureService service = ScreenCaptureService.getInstance();
        int count = service.getCaptureCounter();
        System.out.println("Número de capturas realizadas en esta sesión: " + count);
    }
}

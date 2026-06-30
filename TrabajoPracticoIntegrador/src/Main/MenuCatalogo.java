/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Main;

import service.CategoriaService;
import service.ProductoService;

import java.util.Scanner;

import static java.lang.IO.println;

/**
 *
 * @author valen
 */
public class MenuCatalogo {

    private final Scanner scanner = new Scanner(System.in);
    private final CategoriaService categoriaService = new CategoriaService();
    private final ProductoService productoService = new ProductoService(categoriaService);

    public static void main(String[] args) {
        new MenuCatalogo().run();
    }

    public void run() {
        boolean running = true;
        while (running) {
            mostrarMenu();
            int op = leerInt("Seleccione: ");
            try {
                switch (op) {
                    case 1 -> menuCategorias();
                    case 2 -> menuProductos();
                    case 0 -> {
                        println("Saliendo del módulo catálogo...");
                        running = false;
                    }
                    default -> println("Opción inválida.");
                }
            } catch (Exception e) {
                println("Error: " + e.getMessage());
            }
        }
        scanner.close();
    }

    private void mostrarMenu() {
        println("\n=== MÓDULO CATÁLOGO (DEV 1) ===");
        println("1. Categorías");
        println("2. Productos");
        println("0. Salir");
    }

    // --- Categorías ---
    private void menuCategorias() {
        boolean volver = false;
        while (!volver) {
            println("\n--- CATEGORÍAS ---");
            println("1. Listar");
            println("2. Crear");
            println("3. Editar");
            println("4. Eliminar");
            println("0. Volver");
            int op = leerInt("Seleccione: ");
            try {
                switch (op) {
                    case 1 -> listarCategorias();
                    case 2 -> crearCategoria();
                    case 3 -> editarCategoria();
                    case 4 -> eliminarCategoria();
                    case 0 -> volver = true;
                    default -> println("Opción inválida.");
                }
            } catch (Exception e) {
                println("Error: " + e.getMessage());
            }
        }
    }

    private void listarCategorias() {
        var lista = categoriaService.listar();
        if (lista.isEmpty()) {
            println("No hay categorías cargadas.");
            return;
        }
        println("ID | Nombre | Descripción");
        lista.forEach(c -> println(c.getId() + " | " + c.getNombre() + " | " + c.getDescripcion()));
    }

    private void crearCategoria() {
        String nombre = leerString("Nombre: ");
        String descripcion = leerString("Descripción: ");
        Categoria c = categoriaService.crear(nombre, descripcion);
        println("Categoría creada con id: " + c.getId());
    }

    private void editarCategoria() {
        listarCategorias();
        Long id = leerLong("Id de la categoría a editar: ");
        String nombre = leerStringOpcional("Nuevo nombre (enter para mantener): ");
        String descripcion = leerStringOpcional("Nueva descripción (enter para mantener): ");
        Categoria c = categoriaService.editar(id,
                nombre.isBlank() ? null : nombre,
                descripcion.isBlank() ? null : descripcion);
        println("Categoría actualizada: " + c);
    }

    private void eliminarCategoria() {
        listarCategorias();
        Long id = leerLong("Id de la categoría a eliminar: ");
        String confirm = leerString("Confirma eliminación S/N: ");
        if (confirm.equalsIgnoreCase("S")) {
            categoriaService.eliminar(id);
            println("Categoría eliminada (soft delete).");
        } else {
            println("Operación cancelada.");
        }
    }

    // --- Productos ---
    private void menuProductos() {
        boolean volver = false;
        while (!volver) {
            println("\n--- PRODUCTOS ---");
            println("1. Listar");
            println("2. Crear");
            println("3. Editar");
            println("4. Eliminar");
            println("0. Volver");
            int op = leerInt("Seleccione: ");
            try {
                switch (op) {
                    case 1 -> listarProductos();
                    case 2 -> crearProducto();
                    case 3 -> editarProducto();
                    case 4 -> eliminarProducto();
                    case 0 -> volver = true;
                    default -> println("Opción inválida.");
                }
            } catch (Exception e) {
                println("Error: " + e.getMessage());
            }
        }
    }

    private void listarProductos() {
        var lista = productoService.listar();
        if (lista.isEmpty()) {
            println("No hay productos cargados.");
            return;
        }
        println("ID | Nombre | Precio | Stock | Categoria");
        lista.forEach(p -> println(p.getId() + " | " + p.getNombre() + " | " + p.getPrecio() + " | " + p.getStock() + " | " +
                (p.getCategoria() == null ? "N/A" : p.getCategoria().getNombre())));
    }

    private void crearProducto() {
        String nombre = leerString("Nombre: ");
        String descripcion = leerString("Descripción: ");
        double precio = leerDouble("Precio: ");
        int stock = leerInt("Stock: ");
        String imagen = leerStringOpcional("Imagen (enter para omitir): ");
        boolean disponible = leerBoolean("Disponible? (S/N): ");
        listarCategorias();
        Long idCat = leerLong("Id de categoría: ");
        Producto p = productoService.crear(nombre, descripcion, precio, stock,
                imagen.isBlank() ? null : imagen, disponible, idCat);
        println("Producto creado con id: " + p.getId());
    }

    private void editarProducto() {
        listarProductos();
        Long id = leerLong("Id del producto a editar: ");
        String nombre = leerStringOpcional("Nuevo nombre (enter para mantener): ");
        String descripcion = leerStringOpcional("Nueva descripción (enter para mantener): ");
        String precioStr = leerStringOpcional("Nuevo precio (enter para mantener): ");
        Double precio = precioStr.isBlank() ? null : Double.parseDouble(precioStr);
        String stockStr = leerStringOpcional("Nuevo stock (enter para mantener): ");
        Integer stock = stockStr.isBlank() ? null : Integer.parseInt(stockStr);
        String imagen = leerStringOpcional("Nueva imagen (enter para mantener): ");
        String dispStr = leerStringOpcional("Disponible? S/N (enter para mantener): ");
        Boolean disponible = dispStr.isBlank() ? null : dispStr.equalsIgnoreCase("S");
        listarCategorias();
        String idCatStr = leerStringOpcional("Id de nueva categoría (enter para mantener): ");
        Long idCat = idCatStr.isBlank() ? null : Long.parseLong(idCatStr);

        Producto p = productoService.editar(id,
                nombre.isBlank() ? null : nombre,
                descripcion.isBlank() ? null : descripcion,
                precio,
                stock,
                imagen.isBlank() ? null : imagen,
                disponible,
                idCat);
        println("Producto actualizado: " + p);
    }

    private void eliminarProducto() {
        listarProductos();
        Long id = leerLong("Id del producto a eliminar: ");
        String confirm = leerString("Confirma eliminación S/N: ");
        if (confirm.equalsIgnoreCase("S")) {
            productoService.eliminar(id);
            println("Producto eliminado (soft delete).");
        } else {
            println("Operación cancelada.");
        }
    }

    // --- Helpers ---
    private int leerInt(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String line = scanner.nextLine();
                return Integer.parseInt(line.trim());
            } catch (NumberFormatException e) {
                println("Entrada inválida. Ingrese un número entero.");
            }
        }
    }

    private long leerLong(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String line = scanner.nextLine();
                return Long.parseLong(line.trim());
            } catch (NumberFormatException e) {
                println("Entrada inválida. Ingrese un número entero largo.");
            }
        }
    }

    private double leerDouble(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String line = scanner.nextLine();
                return Double.parseDouble(line.trim());
            } catch (NumberFormatException e) {
                println("Entrada inválida. Ingrese un número válido (decimal).");
            }
        }
    }

    private String leerString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private String leerStringOpcional(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    private boolean leerBoolean(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            if (line.equalsIgnoreCase("S")) return true;
            if (line.equalsIgnoreCase("N")) return false;
            println("Entrada inválida. Ingrese S o N.");
        }
    }

    private void println(String msg) {
        System.out.println(msg);
    }
}
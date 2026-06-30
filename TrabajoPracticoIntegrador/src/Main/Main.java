/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Main;

import enums.Rol;
import exception.EntityNotFoundException;
import java.util.Scanner;
import service.CategoriaService;
import service.PedidoService;
import service.ProductoService;
import service.UsuarioService;

/**
 *
 * @author valen
 */
public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        UsuarioService usuarioService = new UsuarioService();
        CategoriaService categoriaService = new CategoriaService();
        ProductoService productoService = new ProductoService();
        PedidoService pedidoService = new PedidoService();

        int opcionPrincipal = -1;

        System.out.println("BIENVENIDO A FOOD STORE");
        while (opcionPrincipal != 0) {
            System.out.println("\nSISTEMA DE PEDIDOS");
            System.out.println("1. Categorías");
            System.out.println("2. Productos");
            System.out.println("3. Usuarios");
            System.out.println("4. Pedidos");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opción: ");

            try {
                opcionPrincipal = sc.nextInt();
                switch (opcionPrincipal) {
                    case 1:
                        menuCategorias(sc, categoriaService);
                        break;
                    case 2:
                        menuProductos(sc, productoService);
                        break;
                    case 3:
                        menuUsuarios(sc, usuarioService);
                        break;
                    case 4:
                        menuPedidos(sc, pedidoService);
                        break;
                    case 0:
                        System.out.println("Cerrando el sistema...");
                        break;
                    default:
                        System.out.println("Opción inválida. Por favor, seleccione un número del 0 al 4.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Debe ingresar un número válido.");
            }
        }

        // Cerramos el scanner al terminar el programa
        sc.close();
    }

    private static void menuUsuarios(Scanner sc, UsuarioService usuarioService) {
        int opcionUsuario = -1;

        while (opcionUsuario != 0) {
            System.out.println("GESTIÓN DE USUARIOS");
            System.out.println("1. Listar usuarios");
            System.out.println("2. Crear usuario");
            System.out.println("3. Editar usuario");
            System.out.println("4. Eliminar usuario");
            System.out.println("0. Volver al menú principal");
            System.out.print("Seleccione una opción: ");

            try {
                opcionUsuario = Integer.parseInt(sc.nextLine());

                switch (opcionUsuario) {
                    case 1:
                        usuarioService.listarUsuarios();
                        break;
                    case 2:
                        System.out.println("CREAR NUEVO USUARIO");
                        System.out.print("Ingrese nombre: ");
                        String nombre = sc.nextLine();
                        System.out.print("Ingrese apellido: ");
                        String apellido = sc.nextLine();
                        System.out.print("Ingrese mail: ");
                        String mail = sc.nextLine();
                        System.out.print("Ingrese celular: ");
                        String celular = sc.nextLine();
                        System.out.print("Ingrese contraseña: ");
                        String pass = sc.nextLine();

                        System.out.print("Seleccione rol (1. ADMIN, 2. USUARIO): ");
                        String rolOpcion = sc.nextLine();
                        Rol rol = (rolOpcion.equals("1")) ? Rol.ADMIN : Rol.USUARIO;

                        usuarioService.crearUsuario(nombre, apellido, mail, celular, pass, rol);
                        break;
                    case 3:
                        System.out.println("EDITAR USUARIO");
                        System.out.print("Ingrese el ID del usuario a editar: ");
                        Long idEditar = Long.parseLong(sc.nextLine());

                        System.out.print("Ingrese nuevo nombre: ");
                        String nNombre = sc.nextLine();
                        System.out.print("Ingrese nuevo apellido: ");
                        String nApellido = sc.nextLine();
                        System.out.print("Ingrese nuevo mail: ");
                        String nMail = sc.nextLine();
                        System.out.print("Ingrese nuevo celular: ");
                        String nCelular = sc.nextLine();
                        System.out.print("Ingrese nueva contraseña: ");
                        String nPass = sc.nextLine();

                        System.out.print("Seleccione nuevo rol (1. ADMIN, 2. USUARIO): ");
                        String nRolOpcion = sc.nextLine();
                        Rol nRol = (nRolOpcion.equals("1")) ? Rol.ADMIN : Rol.USUARIO;

                        usuarioService.editarUsuario(idEditar, nNombre, nApellido, nMail, nCelular, nPass, nRol);
                        System.out.println("Modificación exitosa");
                        break;
                    case 4:
                        System.out.println(" ELIMINAR USUARIO");
                        System.out.print("Ingrese el ID del usuario a eliminar: ");
                        Long idEliminar = Long.parseLong(sc.nextLine());

                        System.out.print("¿Está seguro que desea eliminar este usuario? (S/N): ");
                        String confirmacion = sc.nextLine();

                        if (confirmacion.equalsIgnoreCase("S")) {
                            usuarioService.eliminarUsuario(idEliminar);
                        } else {
                            System.out.println("Operación cancelada.");
                        }
                        break;
                    case 0:
                        System.out.println("Volviendo al menú principal...");
                        break;
                    default:
                        System.out.println("Opción inválida");
                }
            } catch (NumberFormatException e) {
                System.out.println("Debe ingresar un número válido.");
            } catch (EntityNotFoundException e) {
                // Aquí atrapamos tu excepción personalizada si buscan un ID inexistente
                System.out.println(e.getMessage());
            } catch (Exception e) {
                System.out.println("Ocurrió un error inesperado: " + e.getMessage());
            }
        }
    }


    private static void menuCategorias(Scanner scanner, CategoriaService categoriaService) {
        int opcion = -1;

        while (opcion != 0) {
            System.out.println("GESTIÓN DE CATEGORÍAS");
            System.out.println("1. Listar categorías");
            System.out.println("2. Crear categoría");
            System.out.println("3. Editar categoría");
            System.out.println("4. Eliminar categoría");
            System.out.println("0. Volver al menú principal");
            System.out.print("Seleccione una opción: ");

            try {
                opcion = Integer.parseInt(scanner.nextLine());

                switch (opcion) {
                    case 1:
                        categoriaService.listarCategorias();
                        break;
                    case 2:
                        System.out.println("CREAR CATEGORÍA");
                        System.out.print("Ingrese nombre: ");
                        String nombre = scanner.nextLine();
                        System.out.print("Ingrese descripción: ");
                        String descripcion = scanner.nextLine();
                        categoriaService.crearCategoria(nombre, descripcion);
                        break;
                    case 3:
                        System.out.println("EDITAR CATEGORÍA");
                        System.out.print("Ingrese el ID de la categoría a editar: ");
                        Long idEditar = Long.parseLong(scanner.nextLine());
                        System.out.print("Ingrese nuevo nombre: ");
                        String nNombre = scanner.nextLine();
                        System.out.print("Ingrese nueva descripción: ");
                        String nDescripcion = scanner.nextLine();
                        categoriaService.editarCategoria(idEditar, nNombre, nDescripcion);
                        break;
                    case 4:
                        System.out.println("ELIMINAR CATEGORÍA");
                        System.out.print("Ingrese el ID de la categoría a eliminar: ");
                        Long idEliminar = Long.parseLong(scanner.nextLine());
                        System.out.print("¿Confirma la eliminación? (S/N): ");
                        if (scanner.nextLine().equalsIgnoreCase("S")) {
                            categoriaService.eliminarCategoria(idEliminar);
                        }
                        break;
                    case 0:
                        System.out.println("Volviendo al menú principal...");
                        break;
                    default:
                        System.out.println("Opción inválida.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Debe ingresar un número válido.");
            } catch (EntityNotFoundException e) {
                System.out.println(e.getMessage());
            } catch (Exception e) {
                System.out.println("Ocurrió un error: " + e.getMessage());
            }
        }
    }

 
    private static void menuProductos(Scanner scanner, ProductoService productoService) {
        int opcion = -1;

        while (opcion != 0) {
            System.out.println("GESTIÓN DE PRODUCTOS");
            System.out.println("1. Listar productos");
            System.out.println("2. Crear producto");
            System.out.println("3. Editar producto");
            System.out.println("4. Eliminar producto");
            System.out.println("0. Volver al menú principal");
            System.out.print("Seleccione una opción: ");

            try {
                opcion = Integer.parseInt(scanner.nextLine());

                switch (opcion) {
                    case 1:
                        productoService.listarProductos();
                        break;
                    case 2:
                        System.out.println("CREAR PRODUCTO");
                        System.out.print("Ingrese nombre: ");
                        String nombre = scanner.nextLine();
                        System.out.print("Ingrese precio: ");
                        Double precio = Double.parseDouble(scanner.nextLine());
                        System.out.print("Ingrese descripción: ");
                        String descripcion = scanner.nextLine();
                        System.out.print("Ingrese stock: ");
                        int stock = Integer.parseInt(scanner.nextLine());
                        System.out.print("Ingrese ruta de imagen: ");
                        String imagen = scanner.nextLine();
                        System.out.print("¿Está disponible? (true/false): ");
                        boolean disponible = Boolean.parseBoolean(scanner.nextLine());
                        System.out.print("Ingrese el ID de la Categoría asociada: ");
                        Long idCategoria = Long.parseLong(scanner.nextLine());

                        productoService.crearProducto(nombre, precio, descripcion, stock, imagen, disponible, idCategoria);
                        break;
                    case 3:
                        System.out.println("EDITAR PRODUCTO");
                        System.out.print("Ingrese el ID del producto a editar: ");
                        Long idEditar = Long.parseLong(scanner.nextLine());
                        // Aquí se podrían pedir nuevamente todos los campos para enviarlos al método editar
                        System.out.print("Ingrese nuevo precio: ");
                        Double nPrecio = Double.parseDouble(scanner.nextLine());
                        System.out.print("Ingrese nuevo stock: ");
                        int nStock = Integer.parseInt(scanner.nextLine());

                        productoService.editarProducto(idEditar, nPrecio, nStock); // Versión simplificada
                        break;
                    case 4:
                        System.out.println("ELIMINAR PRODUCTO");
                        System.out.print("Ingrese el ID del producto a eliminar: ");
                        Long idEliminar = Long.parseLong(scanner.nextLine());
                        System.out.print("¿Confirma la eliminación? (S/N): ");
                        if (scanner.nextLine().equalsIgnoreCase("S")) {
                            productoService.eliminarProducto(idEliminar);
                        }
                        break;
                    case 0:
                        System.out.println("Volviendo al menú principal...");
                        break;
                    default:
                        System.out.println("Opción inválida.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Debe ingresar un valor numérico válido donde corresponda.");
            } catch (EntityNotFoundException e) {
                System.out.println(e.getMessage());
            } catch (Exception e) {
                System.out.println("Ocurrió un error: " + e.getMessage());
            }
        }
    }

    private static void menuPedidos(Scanner scanner, PedidoService pedidoService) {
        int opcion = -1;

        while (opcion != 0) {
            System.out.println("GESTIÓN DE PEDIDOS");
            System.out.println("1. Listar pedidos");
            System.out.println("2. Crear pedido");
            System.out.println("3. Actualizar estado / forma de pago");
            System.out.println("4. Eliminar pedido");
            System.out.println("0. Volver al menú principal");
            System.out.print("Seleccione una opción: ");

            try {
                opcion = Integer.parseInt(scanner.nextLine());

                switch (opcion) {
                    case 1:
                        pedidoService.listarPedidos();
                        break;
                    case 2:
                        System.out.println("CREAR PEDIDO");
                        System.out.print("Ingrese el ID del usuario que realiza el pedido: ");
                        Long idUsuario = Long.parseLong(scanner.nextLine());

                        // Aquí el Desarrollador 3 tendrá que implementar un bucle interno en su Servicio
                        // o podemos pasarle el scanner directamente para que cargue los detalles.
                        // Para simplificar la arquitectura, le delegamos la carga de detalles al servicio:
                        pedidoService.crearPedidoInteractiva(idUsuario, scanner);
                        break;
                    case 3:
                        System.out.println("\n-- ACTUALIZAR ESTADO / PAGO --");
                        System.out.print("Ingrese el ID del pedido a actualizar: ");
                        Long idEditar = Long.parseLong(scanner.nextLine());
                        System.out.print("Ingrese nuevo Estado (PENDIENTE, CONFIRMADO, TERMINADO, CANCELADO): ");
                        String estado = scanner.nextLine().toUpperCase();
                        System.out.print("Ingrese nueva Forma de Pago (TARJETA, TRANSFERENCIA, EFECTIVO): ");
                        String formaPago = scanner.nextLine().toUpperCase();

                        pedidoService.actualizarEstadoYFormaPago(idEditar, estado, formaPago);
                        break;
                    case 4:
                        System.out.println("\n-- ELIMINAR PEDIDO --");
                        System.out.print("Ingrese el ID del pedido a eliminar: ");
                        Long idEliminar = Long.parseLong(scanner.nextLine());
                        System.out.print("¿Confirma la eliminación? (S/N): ");
                        if (scanner.nextLine().equalsIgnoreCase("S")) {
                            pedidoService.eliminarPedido(idEliminar);
                        }
                        break;
                    case 0:
                        System.out.println("Volviendo al menú principal...");
                        break;
                    default:
                        System.out.println("Opción inválida.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Debe ingresar un número válido.");
            } catch (EntityNotFoundException e) {
                System.out.println(e.getMessage());
            } catch (Exception e) {
                System.out.println(" Ocurrió un error: " + e.getMessage());
            }
        }
    }
}

package FTP;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

public class main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        //Objeto principal para realizar conexion FTP
        FTPClient ftp = new FTPClient();

        //estado de conexion y autenticacion
        boolean conectado = false;
        boolean logueado = false;

        int opcion;

        do {
            System.out.println("\n\n"); // 
            
            //Menu principal
            System.out.println("==============================");
            System.out.println("-------MENÚ FTP------");
            System.out.println("==============================");
            System.out.println("1. Conectar al servidor");
            System.out.println("2. Login");
            System.out.println("3. Listar archivos raíz /");
            System.out.println("4. Listar archivos /download");
            System.out.println("5. Descargar archivo desde /download");
            System.out.println("6. Subir archivo.doc como archivo2.doc a /uploads");
            System.out.println("7. Desconectar");
            System.out.println("8. Salir");
            System.out.print("Selecciona una opción: ");

            opcion = sc.nextInt();
            sc.nextLine(); //limpiaza buffer

            switch (opcion) {

                //conectar al servidor FTP
                case 1:
                    if (!conectado) {
                        try {
                            System.out.println("Conectando.....");
                            ftp.connect("demo.wftpserver.com"); //direccion servidor
                            conectado = true;
                            System.out.println("Conectado correctamente");
                        } catch (IOException e) {
                            System.out.println("Error al conectar con el servidor");
                        }
                    } else {
                        System.out.println("Ya se encuentra conectado");
                    }
                    break;

                //Conectar con usuario y contraseña
                case 2:
                    if (conectado && !logueado) {
                        try {
                            System.out.println("Realizando login.....");
                            boolean ok = ftp.login("demo", "demo");

                            if (ok) {
                                logueado = true;
                                System.out.println("Login correcto");
                            } else {
                                System.out.println("Login incorrecto");
                            }
                        } catch (IOException e) {
                            System.out.println("Error al intentar hacer login");
                        }
                    } else if (!conectado) {
                        System.out.println("No estas conectado, debes conectarte primero");
                    } else {
                        System.out.println("Ya estas logueado");
                    }
                    break;

                //Listar archivos del directorio raiz
                case 3:
                    if (logueado) {
                        listar(ftp, "/");
                    } else {
                        System.out.println("Debes inicar sesion antes");
                    }
                    break;
                //listar archivos de /download
                case 4:
                    if (logueado) {
                        listar(ftp, "/download");

                    } else {
                        System.out.println("Debes iniciar sesion antes");
                    }
                    break;

                //descargar achivos desde download
                case 5:
                    if (logueado) {
                        try {
                            //cambiar al directorio /download
                            ftp.changeWorkingDirectory("/download");

                            //pedimos el nombre del archivo a descargar
                            System.out.println("Nombre del archivo a descargar");
                            String nombre = sc.nextLine();

                            //modo binario para evitar corrupciones
                            ftp.setFileType(FTP.BINARY_FILE_TYPE);

                            //guardamos el archivo como descargado_nombre.ext
                            try (FileOutputStream fos = new FileOutputStream("descargado_" + nombre)) {
                                //descargamos el archivo y lo guardamos como fos
                                boolean ok = ftp.retrieveFile(nombre, fos);

                                if (ok) {
                                    System.out.println("Archivo descargado correctamente");
                                } else {
                                    System.out.println("No se puede descargar el archivo, ¿Existe?");
                                }
                            }
                        } catch (IOException e) {
                            System.out.println("Error durante la descarga");
                        }
                    } else {
                        System.out.println("Debes conectarte antes");
                    }
                    break;
                    //subir archivo.doc a archivo2.doc en /uploads 
                case 6:
                    if (logueado) {
                        try {
                            //cambiamos el directorio al de subida
                            ftp.changeWorkingDirectory("/upload");

                            //modo binario
                            ftp.setFileType(FTP.BINARY_FILE_TYPE);

                            //Archivo.doc confirmar que existe
                            try (FileInputStream fis = new FileInputStream("archivo.doc")) {
                                //subimos el archivo con otro nombre
                                boolean ok = ftp.storeFile("archivo2.doc", fis);

                                if (ok) {
                                    System.out.println("Archivo subido correctamente");
                                } else {
                                    System.out.println("Error al subir archivos");
                                }
                            }

                        } catch (IOException e) {
                            System.out.println("Error en la subida");
                        } catch (Exception e) {
                            System.out.println("No se encontró el archivo");
                        }
                    } else {
                        System.out.println("Debes estar conectado primero");
                    }
                    break;
                    
                    //Desconectar del servidor y del inicio de sesion
                case 7:
                    try{
                        if (ftp.isConnected()){
                            ftp.logout(); //cerrar sesion
                            ftp.disconnect(); //cerrar conexion
                        }
                        conectado = false;
                        logueado = false;
                        System.out.println("Desconectado del servidor");
                    }catch (IOException e){
                        System.out.println("Error al desconectar");
                    }
                    break;
                    //Salida del programa
                case 8:
                    System.out.println("Saliendo del programa.....");
                    break;
                    
                default: 
                    System.out.println("Indica una opcion del menu");

            }

        } while (opcion != 8);
        sc.close();

    }
    
    //Metodo para listar el directorio o los datos del directorio
    private static void listar(FTPClient ftp, String ruta){
        try{
            System.out.println("Listando contenido de: " + ruta);
            
            //Nos movemos al directorio indicado
            ftp.changeWorkingDirectory(ruta);
            
            //obtenemos la lista de archivos
            FTPFile[] archivos = ftp.listFiles();
            
            //recorremos cada archivo mostrando su nombre
            
            for (FTPFile f: archivos){
                if(f.isDirectory()){
                    System.out.println("DIR" + "  " + f.getName());
                }else{
                    System.out.println("FILE" + "  " + f.getName());
                }
            }
        }catch (IOException e) {
            System.out.println("Error al listar archivos");
        }
    }

}

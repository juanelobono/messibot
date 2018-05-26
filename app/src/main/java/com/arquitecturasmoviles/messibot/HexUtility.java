package com.arquitecturasmoviles.messibot;

import java.util.ArrayList;

/*
 *   Esta clase estática está destinada a utilizarse para facilitar la operación con números hexadecimales
 *   en tiempo de ejecución de la aplicación.
 *
 *   Se retornarán valores nulos en caso de detectarse una excepción durante la ejecución de cualquier método.
 * */
public final class HexUtility {

    private HexUtility() {

    }

    //  Este método recibe un número entero y lo devuelve en hexadecimal
    public static String convertIntegerToHexadecimal(Integer integer) {

        try {
            String hexadecimal = (Integer.toHexString(integer)).toUpperCase();
            return hexadecimal;
        } catch (Exception e) {
            return null;
        }
    }

    //  Este método recibe un número en hexadecimal y lo devuelve en enteros
    public static Integer convertHexadecimalToInteger(String hexadecimal) {
        try {
            String digits = "0123456789ABCDEF";
            hexadecimal = hexadecimal.toUpperCase();
            int integer = 0;

            //  Por cada caracter en el hexadecimal
            for (int i = 0; i < hexadecimal.length(); i++) {
                //  Convertir el caracter en entero
                char character = hexadecimal.charAt(i);
                int digit = digits.indexOf(character);
                integer = 16 * integer + digit;
            }
            return integer;
        } catch (Exception e) {
            return null;
        }
    }

    //  Operación binaria de números hexadecimales, devuelve el resultado en hexadecimales
    public static String addHexadecimals(String hexadecimal1, String hexadecimal2) {
        try {
            int integer1 = convertHexadecimalToInteger(hexadecimal1);
            int integer2 = convertHexadecimalToInteger(hexadecimal2);

            int sum = integer1 + integer2;

            return convertIntegerToHexadecimal(sum);
        } catch (Exception e) {
            return null;
        }
    }

    //  Sobrecarga del método anterior; suma entre sí todos los elementos dentro de una lista de hexadecimales, y devuelve el resultado en hexadecimales
    public static String addHexadecimals(ArrayList<String> hexadecimalList) {
        try {
            String result = "0";

            //  Por cada hexadecimal en la lista de hexadecimales
            for (String hexadecimal : hexadecimalList) {
                //  Sumar hexadecimales
                result = addHexadecimals(hexadecimal, result);
            }

            return result;
        } catch (Exception e) {
            return null;
        }
    }
}

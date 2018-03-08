/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editor;

/**
 * Vyjimka pouzivana v projektu editoru.
 *
 * @author Ondřej Mejzlík
 */
public class ConversionException extends Exception {

    public ConversionException(String s, Throwable t) {
        super(s, t);
    }

    public ConversionException(Throwable t) {
        super(t);
    }

    public ConversionException(String s) {
        super(s);
    }

    public ConversionException() {
        super();
    }
}

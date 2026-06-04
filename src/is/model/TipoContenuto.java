package is.model;

public enum TipoContenuto {
    FILM("Film"),
    SERIE_TV("Serie TV"),
    ANIMAZIONE("Animazione");

    private final String label;
    
    TipoContenuto(String label) {
        this.label = label;
    }

    public String getTipo() {
        return label;
    }

    public static TipoContenuto fromString(String text) {
        if(text == null)
            return FILM;
        for(TipoContenuto tipo : TipoContenuto.values ()) {
            if(tipo.getTipo().equalsIgnoreCase(text) || tipo.label.equalsIgnoreCase(text))
                return tipo;
        }
        return FILM;
    }
}

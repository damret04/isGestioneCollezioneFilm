package is.progetto.command;

import java.util.ArrayDeque;
import java.util.Deque;

public class CommandManager {
    private static int MAX_HISTORY = 30; // massimo di operazioni "undo" che si possono fare

    private final Deque<MediaCommand> history = new ArrayDeque<>();
    private final Deque<MediaCommand> redoStack = new ArrayDeque<>();


    // Esegue il comando e lo salva nella cronologia
    public boolean eseguiCommand(MediaCommand command) {
        boolean result = command.esegui();
        if (result) {
            history.push(command);
            redoStack.clear(); // Se viene fatta una nuova azione, si perdono i vecchi "redo"

            // Non teniamo in memoria troppe azioni
            while (history.size() > MAX_HISTORY) {
                history.removeLast();
            }
        }
        return result;
    }

    // Prende l'ultima azione fatta e la annulla
    public MediaCommand annulla() {
        if (!history.isEmpty()) {
            MediaCommand command = history.pop();
            if (command.annulla()) {
                redoStack.push(command); // lo mette nella pila dei "redo"
                return command;
            } else {
                history.push(command); // se l'undo fallisce, lo rimette a posto
            }
        }
        return null;
    }

    // controlla se si possono fare "undo"
    public boolean canUndo() {
        return !history.isEmpty();
    }

}




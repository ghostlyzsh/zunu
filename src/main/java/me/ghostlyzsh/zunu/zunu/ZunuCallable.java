package me.ghostlyzsh.zunu.zunu;

import java.util.List;

public interface ZunuCallable {
    int arity();
    Object call(Interpreter interpreter, List<Object> arguments);
}

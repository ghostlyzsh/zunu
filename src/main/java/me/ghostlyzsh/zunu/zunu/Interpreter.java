package me.ghostlyzsh.zunu.zunu;

import java.util.ArrayList;
import java.util.List;

public class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void> {
    final Environment globals = new Environment();
    private Environment environment = globals;

    Interpreter() {
        globals.define("clock", new ZunuCallable() {
            @Override
            public int arity() {
                return 0;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                return (double)System.currentTimeMillis() / 1000.0;
            }

            @Override
            public String toString() { return "<native fn>"; }
        });
        globals.define("print", new ZunuCallable() {
            @Override
            public int arity() {
                return -1;
            }

            @Override
            public Object call(Interpreter interpreter, List<Object> arguments) {
                for(Object argument : arguments) {
                    String str = String.valueOf(argument);
                    System.out.print(str);
                }
                System.out.println();
                return null;
            }
        });
    }

    void interpreter(List<Stmt> statements, String name) {
        try {
            for(Stmt statement : statements) {
                execute(statement);
            }
        } catch (RuntimeError e) {
            Zunu.runtimeError(e, name);
        }
    }

    @Override
    public Object visitBinaryExpr(Expr.Binary expr) {
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);

        switch(expr.operator.type) {
            case GREATER:
                if(left instanceof Integer) {
                    if(right instanceof Integer) {
                        return (int)left > (int)right;
                    } else if(right instanceof Float) {
                        return (int)left > (float)right;
                    } else {
                        throw new RuntimeError(expr.operator, "Operands must be a number.");
                    }
                } else if(left instanceof Float) {
                    if(right instanceof Integer) {
                        return (float)left > (int)right;
                    } else if(right instanceof Float) {
                        return (float)left > (float)right;
                    } else {
                        throw new RuntimeError(expr.operator, "Operands must be a number.");
                    }
                } else {
                    throw new RuntimeError(expr.operator, "Operands must be a number.");
                }
            case GREATER_EQUAL:
                if(left instanceof Integer) {
                    if(right instanceof Integer) {
                        return (int)left >= (int)right;
                    } else if(right instanceof Float) {
                        return (int)left >= (float)right;
                    } else {
                        throw new RuntimeError(expr.operator, "Operands must be a number.");
                    }
                } else if(left instanceof Float) {
                    if(right instanceof Integer) {
                        return (float)left >= (int)right;
                    } else if(right instanceof Float) {
                        return (float)left >= (float)right;
                    } else {
                        throw new RuntimeError(expr.operator, "Operands must be a number.");
                    }
                } else {
                    throw new RuntimeError(expr.operator, "Operands must be a number.");
                }
            case LESS:
                if(left instanceof Integer) {
                    if(right instanceof Integer) {
                        return (int)left < (int)right;
                    } else if(right instanceof Float) {
                        return (int)left < (float)right;
                    } else {
                        throw new RuntimeError(expr.operator, "Operands must be a number.");
                    }
                } else if(left instanceof Float) {
                    if(right instanceof Integer) {
                        return (float)left < (int)right;
                    } else if(right instanceof Float) {
                        return (float)left < (float)right;
                    } else {
                        throw new RuntimeError(expr.operator, "Operands must be a number.");
                    }
                } else {
                    throw new RuntimeError(expr.operator, "Operands must be a number.");
                }
            case LESS_EQUAL:
                if(left instanceof Integer) {
                    if(right instanceof Integer) {
                        return (int)left <= (int)right;
                    } else if(right instanceof Float) {
                        return (int)left <= (float)right;
                    } else {
                        throw new RuntimeError(expr.operator, "Operands must be a number.");
                    }
                } else if(left instanceof Float) {
                    if(right instanceof Integer) {
                        return (float)left <= (int)right;
                    } else if(right instanceof Float) {
                        return (float)left <= (float)right;
                    } else {
                        throw new RuntimeError(expr.operator, "Operands must be a number.");
                    }
                } else {
                    throw new RuntimeError(expr.operator, "Operands must be a number.");
                }
            case BANG_EQUAL: return !isEqual(left, right);
            case EQUAL_EQUAL: return isEqual(left, right);
            case MINUS:
                if(left instanceof Integer) {
                    if(right instanceof Integer) {
                        return (int)left - (int) right;
                    } else if(right instanceof Float){
                        return (int)left - (float) right;
                    } else {
                        throw new RuntimeError(expr.operator, "Operands must be a number.");
                    }
                } else if(left instanceof Float) {
                    if(right instanceof Integer) {
                        return (float)left - (int) right;
                    } else if(right instanceof Float){
                        return (float)left - (float) right;
                    } else {
                        throw new RuntimeError(expr.operator, "Operands must be a number.");
                    }
                }  else {
                    throw new RuntimeError(expr.operator, "Operands must be a number.");
                }
            case PLUS:
                if(left instanceof Integer) {
                    if(right instanceof Integer) {
                        return (int)left + (int)right;
                    } else if(right instanceof Float) {
                        return (int)left + (float)right;
                    }
                } else if(left instanceof Float) {
                    if(right instanceof Integer) {
                        return (float)left + (int)right;
                    } else if(right instanceof Float) {
                        return (float)left + (float)right;
                    }
                }

                if(left instanceof String && right instanceof String) {
                    return (String)left + (String)right;
                }

                throw new RuntimeError(expr.operator, "Operands must be two numbers or strings.");
            case SLASH:
                if(left instanceof Integer) {
                    if(right instanceof Integer) {
                        return (int)left / (int) right;
                    } else if(right instanceof Float){
                        return (int)left / (float) right;
                    } else {
                        throw new RuntimeError(expr.operator, "Operands must be a number.");
                    }
                } else if(left instanceof Float) {
                    if(right instanceof Integer) {
                        return (float)left / (int) right;
                    } else if(right instanceof Float) {
                        return (float)left / (float) right;
                    } else {
                        throw new RuntimeError(expr.operator, "Operands must be a number.");
                    }
                } else {
                    throw new RuntimeError(expr.operator, "Operands must be a number.");
                }
            case STAR:
                if(left instanceof Integer) {
                    if(right instanceof Integer) {
                        return (int)left * (int) right;
                    } else if(right instanceof Float){
                        return (int)left * (float) right;
                    } else {
                        throw new RuntimeError(expr.operator, "Operands must be a number.");
                    }
                } else if(left instanceof Float){
                    if(right instanceof Integer) {
                        return (float)left * (int) right;
                    } else if(right instanceof Float){
                        return (float)left * (float) right;
                    } else {
                        throw new RuntimeError(expr.operator, "Operands must be a number.");
                    }
                } else {
                    throw new RuntimeError(expr.operator, "Operands must be a number.");
                }
        }

        return null;
    }

    @Override
    public Object visitCallExpr(Expr.Call expr) {
        Object callee = evaluate(expr.callee);

        List<Object> arguments = new ArrayList<>();
        for(Expr argument : expr.arguments) {
            arguments.add(evaluate(argument));
        }

        if(!(callee instanceof ZunuCallable)) {
            throw new RuntimeError(expr.paren, "Can only call functions and classes.");
        }

        ZunuCallable function = (ZunuCallable) callee;

        if(function.arity() != -1) {
            if (arguments.size() != function.arity()) {
                throw new RuntimeError(expr.paren, "Expected " + function.arity() +
                        " arguments but got " + arguments.size());
            }
        }
        return function.call(this, arguments);
    }

    @Override
    public Object visitGroupingExpr(Expr.Grouping expr) {
        return evaluate(expr.expression);
    }

    @Override
    public Object visitLiteralExpr(Expr.Literal expr) {
        return expr.value;
    }

    @Override
    public Object visitLogicalExpr(Expr.Logical expr) {
        Object left = evaluate(expr.left);

        if(expr.operator.type == TokenType.OR) {
            if(isTruthy(left)) return left;
        } else {
            if(!isTruthy(left)) return left;
        }

        return evaluate(expr.right);
    }

    @Override
    public Object visitUnaryExpr(Expr.Unary expr) {
        Object right = evaluate(expr.right);

        switch (expr.operator.type) {
            case BANG:
                return !isTruthy(right);
            case MINUS:
                if(right instanceof Integer) {
                    return -(int) right;
                } else if(right instanceof Float){
                    return -(float) right;
                } else {
                    throw new RuntimeError(expr.operator, "Operand must be a number.");
                }
        }

        return null;
    }

    @Override
    public Object visitVariableExpr(Expr.Variable expr) {
        return environment.get(expr.name);
    }

    private boolean isTruthy(Object object) {
        if(object == null) return false;
        if(object instanceof Boolean) return (boolean)object;
        return true;
    }

    private boolean isEqual(Object a, Object b) {
        if(a == null && b == null) return true;
        if(a == null) return false;

        return a.equals(b);
    }

    private String stringify(Object object) {
        if(object == null) return "null";

        return object.toString();
    }

    private Object evaluate(Expr expr) {
        return expr.accept(this);
    }

    private void execute(Stmt stmt) {
        stmt.accept(this);
    }

    void executeBlock(List<Stmt> statements, Environment environment) {
        Environment previous = this.environment;
        try {
            this.environment = environment;

            for(Stmt statement: statements) {
                execute(statement);
            }
        } finally {
            this.environment = previous;
        }
    }

    @Override
    public Void visitBlockStmt(Stmt.Block stmt) {
        executeBlock(stmt.statements, new Environment(environment));
        return null;
    }

    @Override
    public Void visitExpressionStmt(Stmt.Expression stmt) {
        evaluate(stmt.expression);
        return null;
    }

    @Override
    public Void visitFunctionStmt(Stmt.Function stmt) {
        ZunuFunction function = new ZunuFunction(stmt);
        environment.define(stmt.name.lexeme, function);
        return null;
    }

    @Override
    public Void visitIfStmt(Stmt.If stmt) {
        if(isTruthy(evaluate(stmt.condition))) {
            execute(stmt.thenBranch);
        } else if (stmt.elseBranch != null) {
            execute(stmt.elseBranch);
        }
        return null;
    }

    public Void visitLetStmt(Stmt.Let stmt) {
        Object value = null;
        if(stmt.initializer != null) {
            value = evaluate(stmt.initializer);
        }

        environment.define(stmt.name.lexeme, value);
        return null;
    }

    @Override
    public Void visitWhileStmt(Stmt.While stmt) {
        while(isTruthy(evaluate(stmt.condition))) {
            try {
                execute(stmt.body);
            } catch(Break b) {
                break;
            } catch(Continue b) {
                continue;
            }
        }
        return null;
    }

    @Override
    public Void visitBreakStmt(Stmt.Break stmt) {
        throw new Break();
    }

    @Override
    public Void visitContinueStmt(Stmt.Continue stmt) {
        throw new Continue();
    }

    public Object visitAssignExpr(Expr.Assign expr) {
        Object value = evaluate(expr.value);
        environment.assign(expr.name, value);
        return value;
    }
}

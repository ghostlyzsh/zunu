package me.ghostlyzsh.suka.suka;

import java.util.List;

public class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void> {

    void interpreter(List<Stmt> statements, String name) {
        try {
            for(Stmt statement : statements) {
                execute(statement);
            }
        } catch (RuntimeError e) {
            Suka.runtimeError(e, name);
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
    public Object visitGroupingExpr(Expr.Grouping expr) {
        return evaluate(expr.expression);
    }

    @Override
    public Object visitLiteralExpr(Expr.Literal expr) {
        return expr.value;
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

    @Override
    public Void visitExpressionStmt(Stmt.Expression stmt) {
        evaluate(stmt.expression);
        return null;
    }

    @Override
    public Void visitPrintStmt(Stmt.Print stmt) {
        Object value = evaluate(stmt.expression);
        System.out.println(stringify(value));
        return null;
    }
}

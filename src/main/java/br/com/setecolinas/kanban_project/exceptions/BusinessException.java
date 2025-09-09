package br.com.setecolinas.kanban_project.exceptions;

public class BusinessException extends RuntimeException {
    public BusinessException(String msg){ super(msg); }
}

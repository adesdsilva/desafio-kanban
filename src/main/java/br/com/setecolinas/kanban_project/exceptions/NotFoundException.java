package br.com.setecolinas.kanban_project.exceptions;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String msg){ super(msg); }
}

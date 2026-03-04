package br.com.setecolinas.kanban_project.exceptions;

public class ResourceNotFoundException extends NotFoundException {
    public ResourceNotFoundException(String msg) {
        super(msg);
    }
}


package de.jodabyte.apisecurity.bola;

import org.springframework.stereotype.Service;

@Service
public class DocumentService {

    private final DocumentRepository repository;

    public DocumentService(DocumentRepository repository) {
        this.repository = repository;
    }

    @HasDocumentReadAccess
    public Document getDocumentById(Long id) {
        return this.repository.findById(id).orElseThrow();
    }
}

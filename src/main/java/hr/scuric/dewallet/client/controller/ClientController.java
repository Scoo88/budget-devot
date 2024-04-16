package hr.scuric.dewallet.client.controller;

import hr.scuric.dewallet.client.models.request.ClientRequest;
import hr.scuric.dewallet.client.models.response.ClientResponse;
import hr.scuric.dewallet.client.service.ClientService;
import hr.scuric.dewallet.common.exceptions.DeWalletException;
import hr.scuric.dewallet.common.swagger.OpenApiTags;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/budget")
public class ClientController {

    private final ClientService clientService;

    @PostMapping("/registration")
    @Operation(tags = OpenApiTags.CLIENT, summary = "Register new client.")
    public ResponseEntity<ClientResponse> registerClient(@Valid @RequestBody ClientRequest request) {
        ClientResponse response = clientService.registerClient(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/client")
    @Operation(tags = OpenApiTags.CLIENT, summary = "Get client.")
    public ResponseEntity<ClientResponse> getClient() throws DeWalletException {
        ClientResponse response = clientService.getClient();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/client")
    @Operation(tags = OpenApiTags.CLIENT, summary = "Update client.")
    public ResponseEntity<ClientResponse> updateClient(@Valid @RequestBody ClientRequest request) throws DeWalletException {
        ClientResponse response = clientService.updateClient(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/client")
    @Operation(tags = OpenApiTags.CLIENT, summary = "Delete client.")
    public ResponseEntity<ClientResponse> deleteClient() throws DeWalletException {
        HttpStatus response = clientService.deleteClient();
        return new ResponseEntity<>(response);
    }
}

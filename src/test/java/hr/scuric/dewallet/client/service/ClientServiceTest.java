//package hr.scuric.dewallet.client.service;
//
//import hr.scuric.dewallet.client.models.request.ClientRequest;
//import hr.scuric.dewallet.client.models.response.ClientResponse;
//import hr.scuric.dewallet.client.repository.ClientRepository;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.time.LocalDate;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//
//@ExtendWith((MockitoExtension.class))
//@Slf4j
//class ClientServiceTest {
//    @Mock
//    private ClientRepository clientRepository;
//    //When using Mockito Use @InjectMocks to inject
//    //Mocked beans to following class
//    @InjectMocks
//    private ClientService clientService;
//
//    @BeforeEach
//    void setUp() {
//    }
//
//    @Test
//    void registerClient() {
//        ClientRequest request = new ClientRequest();
//        request.setFirstName("First");
//        request.setLastName("Last");
//        request.setDateOfBirth(LocalDate.of(1988, 11, 22));
//        request.setPhone("003859112345678");
//        request.setEmail("test@email.com");
//        request.setPassword("test");
//
//        ClientResponse clientResponse = clientService.registerClient(request);
//        assertThat(clientResponse.getEmail().equals(request.getEmail()));
//        log.info(clientResponse.toString());
//    }
//
//    @Test
//    void getClients() {
//    }
//
//    @Test
//    void getClient() {
//    }
//
//    @Test
//    void updateClient() {
//    }
//
//    @Test
//    void deleteClient() {
//    }
//
//    @Test
//    void handleClientBalance() {
//    }
//
//    @Test
//    void handleBalance() {
//    }
//}
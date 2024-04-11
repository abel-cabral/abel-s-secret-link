package org.acme;

import java.util.List;
import org.acme.repository.URLRepository;
import org.acme.Util.ResponseMessage;
import org.acme.Util.URLCrypto;
import org.acme.model.URL;
import org.acme.model.dto.DecryptDto;
import org.acme.model.dto.EncryptDto;
import org.acme.model.dto.URLDTO;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/")
public class URLResource {
	private URLRepository urlRepository;

    @Inject
    public URLResource(URLRepository urlRepository) {
        this.urlRepository = urlRepository;
    };
    
    @POST
    @Path("/encrypt")
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(@Valid EncryptDto decryptDto) {
        String url = decryptDto.getUrl();
        String senha = decryptDto.getSenha();
        Boolean auto_delete = decryptDto.getAuto_delete();
        int numero_exibicao = decryptDto.getNumero_exibicao();

        try {
            URL urlData = new URL(URLCrypto.encryptURL(url, senha), auto_delete, numero_exibicao);
            urlRepository.persistURL(urlData);
            
            return Response.status(Response.Status.CREATED).entity(urlData).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                       .entity(new ResponseMessage(false, e.getMessage()))
                       .build();
        }
    }

    @GET
    @Path("/decrypt/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response encript_link(@Valid DecryptDto decryptDto) {
        try {
            URL urlData = urlRepository.findByEncryptedUrl(decryptDto.getId());
            URLDTO url_dto = new URLDTO(urlData.getUrl());
            url_dto.setUrl(URLCrypto.decryptURL(urlData.getUrl(), decryptDto.getSenha()));
            return Response.status(Response.Status.FOUND).entity(url_dto)
            .build();
        } catch (Exception e) {
            return Response.status(Response.Status.NOT_FOUND)
                       .entity(new ResponseMessage(false, "Link não encontrado"))
                       .build();
        }
    }

    @GET
    @Path("/decrypt/all")
    @Produces(MediaType.APPLICATION_JSON)
    public List<URL> decript_links() {
    	return urlRepository.listAll();
    }
}

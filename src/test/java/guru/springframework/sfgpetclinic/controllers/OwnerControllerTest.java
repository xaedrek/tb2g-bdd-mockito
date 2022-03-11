package guru.springframework.sfgpetclinic.controllers;

import guru.springframework.sfgpetclinic.fauxspring.BindingResult;
import guru.springframework.sfgpetclinic.fauxspring.Model;
import guru.springframework.sfgpetclinic.model.Owner;
import guru.springframework.sfgpetclinic.services.OwnerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class OwnerControllerTest {

    @Mock
    OwnerService ownerService;

    @InjectMocks
    OwnerController ownerController;

    @Mock
    BindingResult bindingResult;

    @Mock
    Model model;

    @Captor
    ArgumentCaptor<String> captor;

    @BeforeEach
    void setUp(){
        given(ownerService.findAllByLastNameLike(captor.capture()))
                .willAnswer(invocationOnMock -> {
                    List<Owner> ownerList = new ArrayList<>();
                    String name = invocationOnMock.getArgument(0);
                    if(name.equals("%romero%")){
                        ownerList.add(new Owner(1L,"rod","romero"));
                      //  return ownerList;
                    }else if(name.equals("%vigano%")){
                        ownerList.add(new Owner(1L,"elisa","romero"));
                        ownerList.add(new Owner(1L,"eva","romero"));
                    //    return ownerList;
                    }
                        return ownerList;
                });
    }

    @Test
    void processFindFormWildCardStrings() {
        //given
        Owner owner = new Owner(1L,"roderick","romero");
        //final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class); inline en vez de la propiedad anotada
        //List<Owner> ownerList = new ArrayList<>();
        //given(ownerService.findAllByLastNameLike(captor.capture())).willReturn(ownerList); esto es lo que había antes de añadir el setUp
        //when
        String viewName = ownerController.processFindForm(owner,bindingResult,null);
        //then
        assertThat("%romero%").isEqualToIgnoringCase(captor.getValue());
    }

    @Test
    void processFindFormWithAnswers() {
        //given
        final String MATCH_ME_ONE = "redirect:/owners/1";
        final String MATCH_ME_SEVERAL = "owners/ownersList";
        final String MATCH_ME_NONE = "owners/findOwners";
        Owner owner = new Owner(1L,"roderick","vigano");
        InOrder inOrder = Mockito.inOrder(ownerService,model);
        //when
        String viewName = ownerController.processFindForm(owner,bindingResult,model);
        //then
        assertThat(MATCH_ME_SEVERAL).isEqualToIgnoringCase(viewName);

        inOrder.verify(ownerService).findAllByLastNameLike(anyString());
        inOrder.verify(model).addAttribute(anyString(),anyList());

    }

    @DisplayName("check view returned ok")
    @Test
    void processCreationFormOk() {
        //given
        final String MATCH_ME = "redirect:/owners/5";
        Owner owner = new Owner(5L,"rod","romero");
        given(bindingResult.hasErrors()).willReturn(false);
        given(ownerService.save(any(Owner.class))).willReturn(owner);
        //when
        String viewName = ownerController.processCreationForm(owner,bindingResult);
        //then
        assertEquals(MATCH_ME,viewName);
    }

    @DisplayName("check view returned ko")
    @Test
    void processCreationFormErrors() {
        //given
        final String MATCH_ME = "owners/createOrUpdateOwnerForm";
        Owner owner = new Owner(5L,"rod","romero");
        given(bindingResult.hasErrors()).willReturn(true);
        //when
        String viewName = ownerController.processCreationForm(owner,bindingResult);
        //then
        assertEquals(MATCH_ME,viewName);
    }
}
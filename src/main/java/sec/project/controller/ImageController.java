package sec.project.controller;

import sec.project.repository.ImageRepository;
import sec.project.domain.ImageObject;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import sec.project.domain.Account;
import sec.project.repository.AccountRepository;

@Controller
public class ImageController {
   
    @Autowired
    private ImageRepository imageRepository;
    
    @Autowired
    private AccountRepository accountRepository;

    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String list(Authentication authentication, Model model) {
        model.addAttribute("images", imageRepository.findAll(new Sort(Sort.Direction.DESC, "id")));
        
        return "index";
    }
    
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String loginscreen() {
        return "login";
    }

    
    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String registerscreen() {
        return "register";
    }
    
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String register(@RequestParam(value="username") String username, @RequestParam(value="password") String password) {
        Account account = new Account();
        account.setUsername(username);
        account.setPassword(password);
        accountRepository.save(account);
        return "redirect:/user";
    }
    
    
    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public String userlist(Authentication authentication, Model model) {
        model.addAttribute("images", accountRepository.findByUsername(authentication.getName()).getImageObjects());
        return "user";
    }

    @RequestMapping(value = "/images/{id}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> viewImage(@PathVariable Long id) {
        ImageObject fo = imageRepository.findOne(id);
      
            final HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(fo.getContentType()));
            headers.add("Content-Disposition", "attachment; filename=" + fo.getName());
            headers.setContentLength(fo.getContentLength());

            return new ResponseEntity<>(fo.getContent(), headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/images", method = RequestMethod.POST)
    public String addImage(Authentication authentication, @RequestParam("image") MultipartFile image, @RequestParam(value="description", required=false) String description) throws IOException {
        Account account = accountRepository.findByUsername(authentication.getName());
        
        ImageObject imageObject = new ImageObject();
        imageObject.setContentType(image.getContentType());
        imageObject.setContent(image.getBytes());
        imageObject.setName(image.getOriginalFilename());
        imageObject.setContentLength(image.getSize());
        imageObject.setDescription(description);
        imageObject.setAccount(account);
        imageRepository.save(imageObject);
       
        return "redirect:/user";
    }

    @RequestMapping(value = "/images/{id}/delete", method = RequestMethod.GET)
    public String delete_insecure(Authentication authentication, @PathVariable Long id) {
        imageRepository.delete(id);

        return "redirect:/user";
    }
    
    @RequestMapping(value = "/changepw", method = RequestMethod.GET)
    public String change_password(Authentication authentication, @RequestParam(value="oldpassword", required=false) String oldpassword, @RequestParam(value="newpassword") String newpassword) {
        Account account = accountRepository.findByUsername(authentication.getName());
        if(account.getPassword().equals(oldpassword)){

        }
        account.setPassword(newpassword);
        accountRepository.save(account);

        return "redirect:/user";
    }

    @RequestMapping(value = "/images/{id}", method = RequestMethod.DELETE)
    public String delete_secure(Authentication authentication, @PathVariable Long id) {
        if(imageRepository.findOne(id).getAccount().getUsername() == null ? authentication.getName() == null : imageRepository.findOne(id).getAccount().getUsername().equals(authentication.getName()))
        {
        imageRepository.delete(id);
        }
        return "redirect:/user";
    }
}

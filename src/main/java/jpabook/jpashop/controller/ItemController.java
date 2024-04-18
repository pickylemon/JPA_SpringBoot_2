package jpabook.jpashop.controller;

import jpabook.jpashop.domain.items.Book;
import jpabook.jpashop.domain.items.Item;
import jpabook.jpashop.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Slf4j
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping("/new")
    public String createForm(Model model){
        model.addAttribute("form", new BookForm());
        return "items/createItemForm";
    }

    @PostMapping("/new")
    public String create(BookForm form, BindingResult result, RedirectAttributes redirectAttributes){
        if(result.hasErrors()) {
            return "redirect:/items/new";
        }

        Book book = form.toBook();
        itemService.saveItem(book);

        long itemId = book.getId();

        return "redirect:/items/{itemId}";
    }

    @GetMapping("/{itemId}")
    public String ItemDetail(Model model, @PathVariable Long itemId){
        Item item = itemService.findOne(itemId);
        model.addAttribute("item", item);
        return "items/itemDetail";
    }
}

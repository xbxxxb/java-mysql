package com.bookstore.controller;

import com.bookstore.model.Tag;
import com.bookstore.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tags")
public class TagController {
    @Autowired
    private TagRepository tagRepository;

    @GetMapping
    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }

    @PostMapping
    public Tag createTag(@RequestBody Tag tag) {
        return tagRepository.save(tag);
    }

    @GetMapping("/{id}")
    public Optional<Tag> getTagById(@PathVariable Long id) {
        return tagRepository.findById(id);
    }

    @GetMapping("/name/{name}")
    public Optional<Tag> getTagByName(@PathVariable String name) {
        return tagRepository.findByName(name);
    }

    @PutMapping("/{id}")
    public Tag updateTag(@PathVariable Long id, @RequestBody Tag tagDetails) {
        Tag tag = tagRepository.findById(id).orElse(null);
        if (tag != null) {
            tag.setName(tagDetails.getName());
            return tagRepository.save(tag);
        }
        return null;
    }

    @DeleteMapping("/{id}")
    public void deleteTag(@PathVariable Long id) {
        tagRepository.deleteById(id);
    }
}
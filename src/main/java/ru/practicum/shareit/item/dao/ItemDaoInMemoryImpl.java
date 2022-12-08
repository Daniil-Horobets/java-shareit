package ru.practicum.shareit.item.dao;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.ItemOwnerMismatchException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class ItemDaoInMemoryImpl implements ItemDao {

    private final Map<Long, Item> items = new HashMap<>();
    private long idCounter = 0;

    @Override
    public ItemDto createItem(ItemDto itemDto, long owner) {
        itemDto.setId(++idCounter);
        Item item = Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(owner)
                .request(null)
                .build();
        items.put(item.getId(), item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto updateItem(long id, long owner, ItemDto itemDto) {
        Item item = items.get(id);
        if (item.getOwner() != owner) {
            throw new ItemOwnerMismatchException("User is not the owner of item");
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto getItem(long id) {
        return ItemMapper.toItemDto(items.get(id));
    }

    @Override
    public List<ItemDto> getUserItems(long owner) {
        return items.values().stream()
                .filter(item -> item.getOwner() == owner)
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> findItem(String text) {
        if (text != null && !text.isBlank()) {
            return items.values().stream()
                    .filter(
                            item -> item.getName().toLowerCase().contains(text.toLowerCase()) && item.getAvailable()
                            || item.getDescription().toLowerCase().contains(text.toLowerCase()) && item.getAvailable()
                    ).map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }
}

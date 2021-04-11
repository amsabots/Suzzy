package com.amsabots.suzzy;

import com.amsabots.suzzy.Cart.ProductList;
import com.google.common.base.Predicate;

import java.util.regex.Pattern;
public class ItemFilter implements Predicate<ProductList>
{
    private final Pattern pattern;

    public ItemFilter(final String regex)
    {
        pattern = Pattern.compile(regex);
    }

    @Override
    public boolean apply(final ProductList input)
    {
        return pattern.matcher(input.getName().toLowerCase()).find();
    }
}


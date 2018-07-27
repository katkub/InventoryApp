package com.example.android.inventoryapp.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.R;
import com.example.android.inventoryapp.data.ContractClass.InventoryEntry;

import static java.lang.String.valueOf;

/**
 * {@link CursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of inventory data as its data source. This adapter knows
 * how to create list items for each row of inventory data in the {@link Cursor}.
 */
public class CursorAdapter extends android.widget.CursorAdapter {

    /**
     * Constructs a new {@link CursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public CursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the inventory data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current product can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = view.findViewById(R.id.name);
        TextView summaryTextView = view.findViewById(R.id.summary);
        TextView quantityTextView = view.findViewById(R.id.quantityNumber);
        // Find button sale
        Button sale = view.findViewById(R.id.sale);

        // Find the columns of product that we're interested in
        // Read the product attributes from the Cursor for the current product
        final int productID = cursor.getInt(cursor.getColumnIndex(InventoryEntry._ID));
        String name = cursor.getString(cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME));
        int price = cursor.getInt(cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_PRICE));
        final int quantity = cursor.getInt(cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_QUANTITY));


        // Adding onClickListener to button "sale"
        sale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri currentProductUri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, productID);
                decQuantity(context, currentProductUri, quantity);
            }
        });


        // Update the TextViews with the attributes for the current product
        nameTextView.setText(name);
        summaryTextView.setText(String.valueOf(price));
        quantityTextView.setText(context.getString(R.string.category_quantity) + ": " + valueOf(quantity));

    }

    // Method for decrement quantity when sale button is clicked
    private void decQuantity(Context context, Uri uri, int quantity) {
        if (quantity > 0) {
            int newQuantity = quantity - 1;

            ContentValues values = new ContentValues();
            values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, newQuantity);

            int rowsAffected = context.getContentResolver().update(uri, values, null, null);

            // Show a toast message depending on whether or not the sale was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the sale.
                Toast.makeText(context, R.string.saleEr, Toast.LENGTH_LONG).show();
            } else {
                // Otherwise, the sale was successful and we can display a toast.
                Toast.makeText(context, R.string.saleSuc, Toast.LENGTH_SHORT).show();

            }
        }
    }
}

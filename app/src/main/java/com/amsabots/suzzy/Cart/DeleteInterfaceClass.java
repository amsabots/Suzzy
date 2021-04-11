package com.amsabots.suzzy.Cart;

public class DeleteInterfaceClass {
    AddToCartListener listener;
    private static DeleteInterfaceClass mInstance;
    private int position;
    public interface AddToCartListener{
        void getPositiontoRemove(int position);
    }
    public void setAddToCartListener(AddToCartListener listener){
        this.listener = listener;
    }
    private DeleteInterfaceClass() {
    }
   public static DeleteInterfaceClass getInstance(){
        if(mInstance == null) mInstance= new DeleteInterfaceClass();
        return mInstance;
   }

    public void setPosition(int position) {
       if(listener != null){
           this.position = position;
           listener.getPositiontoRemove(position);
       }
    }
}

package weapons.crafting;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import weapons.tileentity.TileEntityWeaponCarver;

public class ShapedWeaponCarverRecipe implements IRecipy
{
    /** How many horizontal slots this recipe is wide. */
    public final int recipeWidth;

    /** How many vertical slots this recipe uses. */
    public final int recipeHeight;

    /** Is a array of ItemStack that composes the recipe. */
    public final ItemStack[] recipeItems;

    /** Is the ItemStack that you get when craft the recipe. */
    private ItemStack recipeOutput;

    /** Is the itemID of the output item that you get when craft the recipe. */
    public final int recipeOutputItemID;
    private boolean field_92101_f = false;

    public ShapedWeaponCarverRecipe(int par1, int par2, ItemStack[] par3ArrayOfItemStack, ItemStack par4ItemStack)
    {
        this.recipeOutputItemID = par4ItemStack.itemID;
        this.recipeWidth = par1;
        this.recipeHeight = par2;
        this.recipeItems = par3ArrayOfItemStack;
        this.recipeOutput = par4ItemStack;
    }

    public ItemStack getRecipeOutput()
    {
        return this.recipeOutput;
    }

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    public boolean matches(TileEntityWeaponCarver inventory, World par2World)
    {
        for (int i = 0; i <= 3 - this.recipeWidth; ++i)
        {
            for (int j = 0; j <= 3 - this.recipeHeight; ++j)
            {
                if (this.checkMatch(inventory, i, j, true))
                {
                    return true;
                }

                if (this.checkMatch(inventory, i, j, false))
                {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Checks if the region of a crafting inventory is match for the recipe.
     */
    private boolean checkMatch(TileEntityWeaponCarver inventory, int par2, int par3, boolean par4)
    {
        for (int k = 0; k < 3; ++k)
        {
            for (int l = 0; l < 3; ++l)
            {
                int i1 = k - par2;
                int j1 = l - par3;
                ItemStack itemstack = null;

                if (i1 >= 0 && j1 >= 0 && i1 < this.recipeWidth && j1 < this.recipeHeight)
                {
                    if (par4)
                    {
                        itemstack = this.recipeItems[this.recipeWidth - i1 - 1 + j1 * this.recipeWidth];
                    }
                    else
                    {
                        itemstack = this.recipeItems[i1 + j1 * this.recipeWidth];
                    }
                }

                ItemStack itemstack1 = inventory.getStackInRowAndColumn(k, l);

                if (itemstack1 != null || itemstack != null)
                {
                    if (itemstack1 == null && itemstack != null || itemstack1 != null && itemstack == null)
                    {
                        return false;
                    }

                    if (itemstack.itemID != itemstack1.itemID)
                    {
                        return false;
                    }

                    if (itemstack.getItemDamage() != 32767 && itemstack.getItemDamage() != itemstack1.getItemDamage())
                    {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    public ItemStack getCraftingResult(TileEntityWeaponCarver inventory)
    {
        ItemStack itemstack = this.getRecipeOutput().copy();

        if (this.field_92101_f)
        {
            for (int i = 0; i < inventory.getSizeInventory(); ++i)
            {
                ItemStack itemstack1 = inventory.getStackInSlot(i);

                if (itemstack1 != null && itemstack1.hasTagCompound())
                {
                    itemstack.setTagCompound((NBTTagCompound)itemstack1.stackTagCompound.copy());
                }
            }
        }

        return itemstack;
    }

    /**
     * Returns the size of the recipe area
     */
    public int getRecipeSize()
    {
        return this.recipeWidth * this.recipeHeight;
    }

    public ShapedWeaponCarverRecipe func_92100_c()
    {
        this.field_92101_f = true;
        return this;
    }
}

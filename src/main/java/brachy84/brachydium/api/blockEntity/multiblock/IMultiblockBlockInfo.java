package brachy84.brachydium.api.blockEntity.multiblock;

public interface IMultiblockBlockInfo {

    MultiBlockEntity getMultiBlock();

    void setMultiBlock(MultiBlockEntity multiBlock);

    void removeMultiBlock();

    boolean hasMultiBlock();
}

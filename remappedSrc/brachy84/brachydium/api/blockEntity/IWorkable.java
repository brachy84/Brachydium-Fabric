package brachy84.brachydium.api.blockEntity;

public interface IWorkable extends IControllable{

    int getProgress();

    int getDuration();

    boolean isActive();
}

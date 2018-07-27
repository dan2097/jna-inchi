package com.github.dan2097.jnainchi;

public class InchiBond {
  
  private final InchiAtom start;
  private final InchiAtom end;
  private final InchiBondType type;
  private final InchiBondStereo stereo;
  
  public InchiBond(InchiAtom start, InchiAtom end, InchiBondType type) {
    this(start, end, type, InchiBondStereo.NONE);
  }

  public InchiBond(InchiAtom start, InchiAtom end, InchiBondType type, InchiBondStereo stereo) {
    if (start.equals(end)) {
      throw new IllegalArgumentException("start and end must be different atoms");
    }
    if (type == null) {
      throw new IllegalArgumentException("type must not be null");
    }
    if (stereo == null) {
      throw new IllegalArgumentException("stereo must not be null, use InchiBondStereo.NONE");
    }
    this.start = start;
    this.end = end;
    this.type = type;
    this.stereo = stereo;
  }

  public InchiAtom getStart() {
    return start;
  }

  public InchiAtom getEnd() {
    return end;
  }

  public InchiBondType getType() {
    return type;
  }

  public InchiBondStereo getStereo() {
    return stereo;
  }

  public InchiAtom getOther(InchiAtom atom) {
    if (start == atom) {
      return end;
    }
    if (end == atom) {
      return start;
    }
    return null;
  }
  

}

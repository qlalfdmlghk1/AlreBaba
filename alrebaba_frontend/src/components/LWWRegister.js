class LWWRegister {
  constructor( state) {
    this.state = state;
  }

  get value() { 
    return this.state[2];
  }

  set(value) {
    // 로컬 ID로 피어 ID 설정, 로컬 타임스탬프를 1 증가시키고 값 설정
    this.state = [this.state[0], this.state[1] + 1, value];
  }

  merge(state) {
    const [remoteParticipantName, remoteTimestamp] = [state[0], state[1]];
    const [localParticipantName, localTimestamp] = [this.state[0], this.state[1]];
    // 로컬 타임스탬프가 원격 타임스탬프보다 크면 들어오는 값을 무시
    if(remoteTimestamp < 0 && Math.abs(remoteTimestamp) > localTimestamp){
      this.state = state;
      this.state[1] = Math.abs(this.state[1]);
      return;
    }

    if (localTimestamp > remoteTimestamp) return;

    // if (remoteParticipantName === localParticipantName && remoteTimestamp > localTimestamp) return;

    if (localTimestamp === remoteTimestamp && localParticipantName > remoteParticipantName) return;
    console.log("mergeed");
    this.state = state;
  }
}

export default LWWRegister;
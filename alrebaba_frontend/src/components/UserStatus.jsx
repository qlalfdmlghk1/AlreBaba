import React from "react";
import IconOnline from "./Icons/IconOnline";
import IconAway from "./Icons/IconAway";
import IconDoNotDisturb from "./Icons/IconDoNotDisturb";
import IconOffline from "./Icons/IconOffline";

function UserStatus({ status }) {
  if (status === "ONLINE") {
    return <IconOnline />;
  } else if (status === "ON_ANOTHER_BUSINESS") {
    return <IconAway />;
  } else if (status === "NO_INTERFERENCE") {
    return <IconDoNotDisturb />;
  } else {
    return <IconOffline />;
  }
}

export default UserStatus;

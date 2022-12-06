import { faCircleNotch } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

export const Loader = () => {
  return (
    <FontAwesomeIcon className="animate-spin text-3xl" icon={faCircleNotch} />
  );
};

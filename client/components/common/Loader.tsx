import { faCircleNotch } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

export const Loader = () => {
  return (
    <article>
      <FontAwesomeIcon className="text-2xl" icon={faCircleNotch} />
    </article>
  );
};

import { ValidationMsg } from '../common/ValildationMsg';
import type { UseFormRegisterReturn } from 'react-hook-form';

type InputProps = {
  label: string;
  sublabel?: string;
  register: UseFormRegisterReturn;
  errors?: string | undefined;
  type: string;
  placeholder: string;
};

const inputClassName = 'rounded-full w-96 h-10 pl-4 border';
const inputContainerClassName = 'flex flex-col items-start space-y-2';

export const Input = ({
  label,
  sublabel,
  register,
  errors,
  type,
  placeholder,
}: InputProps) => {
  return (
    <section className={inputContainerClassName}>
      <label className="font-bold">
        {label} <span className="text-sm font-normal">{sublabel}</span>
      </label>
      <input
        {...register}
        className={inputClassName}
        type={type}
        placeholder={placeholder}
      />
      <ValidationMsg msg={errors} />
    </section>
  );
};

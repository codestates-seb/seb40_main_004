import { useState } from 'react';
import { Select, SelectOption } from './Select';

//DB에서 넘어올 때 수정할 예정
const options = [
  { label: 'First', values: 1 },
  { label: 'Second', values: 2 },
  { label: 'Third', values: 3 },
  { label: 'Fourth', values: 4 },
  { label: 'Fifth', values: 5 },
];

export const TagSelect = () => {
  const [values, setValues] = useState<SelectOption[]>([options[0]]);

  return (
    <>
      <Select
        multiple
        options={options}
        values={values}
        onChange={(o) => setValues(o)}
      />
    </>
  );
};

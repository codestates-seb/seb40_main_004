import React from 'react';
import { AsideBot } from './AsideBot';
import { AsideMid } from './AsideMid';
import { AsideTop } from './AsideTop';

const AsideComponent = () => {
  return (
    <section className="w-[305px]">
      <AsideTop />
      <AsideMid />
      <AsideBot />
    </section>
  );
};

export default AsideComponent;

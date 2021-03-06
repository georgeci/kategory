package kategory

@instance(Ior::class)
interface IorFunctorInstance<L> : Functor<IorKindPartial<L>> {
    override fun <A, B> map(fa: IorKind<L, A>, f: (A) -> B): Ior<L, B> = fa.ev().map(f)
}

@instance(Ior::class)
interface IorApplicativeInstance<L> : IorFunctorInstance<L>, Applicative<IorKindPartial<L>> {

    fun SL(): Semigroup<L>

    override fun <A> pure(a: A): Ior<L, A> = Ior.Right(a)

    override fun <A, B> map(fa: IorKind<L, A>, f: (A) -> B): Ior<L, B> = fa.ev().map(f)

    override fun <A, B> ap(fa: IorKind<L, A>, ff: IorKind<L, (A) -> B>): Ior<L, B> =
            fa.ev().ap(ff, SL())
}

@instance(Ior::class)
interface IorMonadInstance<L> : IorApplicativeInstance<L>, Monad<IorKindPartial<L>> {

    override fun <A, B> flatMap(fa: IorKind<L, A>, f: (A) -> IorKind<L, B>): Ior<L, B> =
            fa.ev().flatMap({ f(it).ev() }, SL())

    override fun <A, B> ap(fa: IorKind<L, A>, ff: IorKind<L, (A) -> B>): Ior<L, B> =
            fa.ev().ap(ff, SL())

    override fun <A, B> tailRecM(a: A, f: (A) -> IorKind<L, Either<A, B>>): Ior<L, B> =
            Ior.tailRecM(a, f, SL())

}

@instance(Ior::class)
interface IorFoldableInstance<L> : Foldable<IorKindPartial<L>> {

    override fun <B, C> foldL(fa: HK<HK<IorHK, L>, B>, b: C, f: (C, B) -> C): C = fa.ev().foldL(b, f)

    override fun <B, C> foldR(fa: HK<HK<IorHK, L>, B>, lb: Eval<C>, f: (B, Eval<C>) -> Eval<C>): Eval<C> =
            fa.ev().foldR(lb, f)

}

@instance(Ior::class)
interface IorTraverseInstance<L> : IorFoldableInstance<L>, Traverse<IorKindPartial<L>> {

    override fun <G, B, C> traverse(fa: IorKind<L, B>, f: (B) -> HK<G, C>, GA: Applicative<G>): HK<G, Ior<L, C>> =
            fa.ev().traverse(f, GA)

}
